package com.chakray.chakray_api.service;

import com.chakray.chakray_api.model.User;
import com.chakray.chakray_api.model.Address;
import com.chakray.chakray_api.data.UserData;
import com.chakray.chakray_api.utils.AES256Util;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    // aes256Util
    private final AES256Util aes256Util;
    private static final List<User> users = new ArrayList<>(UserData.USERS);

    public UserService(AES256Util aes256Util) {
        this.aes256Util = aes256Util;
    }

    // ===================== Contraseña =====================
    public String encryptPassword(String password) {
        return aes256Util.encrypt(password);
    }

    public User removePassword(User user) {
        user.setPassword(null);
        return user;
    }

    // ===================== Autenticación =====================
    public Optional<User> authenticate(String taxId, String password) {
        Optional<User> userOptional = users.stream()
                .filter(u -> u.getTax_id().equalsIgnoreCase(taxId))
                .findFirst();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hashedInput = encryptPassword(password);

            if (hashedInput.equals(user.getPassword())) {
                return Optional.of(removePassword(user));
            }
        }
        return Optional.empty();
    }

    // ===================== Fecha =====================
    public LocalDateTime getCurrentTimestamp() {
        ZoneId madagascarZone = ZoneId.of("Indian/Antananarivo");
        return LocalDateTime.now(madagascarZone);
    }

    // ===================== Validación RFC =====================
    public boolean validateTaxId(String taxId) {
        Pattern pattern = Pattern.compile(
                "^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]$");
        Matcher matcher = pattern.matcher(taxId.toUpperCase());
        return matcher.matches();
    }

    public boolean isTaxIdUnique(String taxId, UUID currentId) {
        return users.stream()
                .noneMatch(u -> u.getTax_id().equalsIgnoreCase(taxId)
                        && (currentId == null || !u.getId().equals(currentId)));
    }

    // ===================== CRUD =====================
    public List<User> findAllUsers(String sortedBy, String filter) {
        List<User> result = new ArrayList<>(users);

        // ===================== Filtrado =====================
        if (filter != null && !filter.isEmpty()) {
            String[] parts = filter.split("\\+");
            if (parts.length == 3) {
                String attribute = parts[0].trim();
                String operator = parts[1].trim();
                String value = parts[2].trim().toLowerCase().replaceAll("[\\s+]", "");

                result = result.stream()
                        .filter(user -> {
                            String attrValue = getAttributeValue(user, attribute);
                            if (attrValue == null)
                                return false;
                            attrValue = attrValue.toLowerCase().trim();

                            return switch (operator) {
                                case "co" -> attrValue.contains(value);
                                case "eq" -> attrValue.equals(value);
                                case "sw" -> attrValue.startsWith(value);
                                case "ew" -> attrValue.endsWith(value);
                                default -> true;
                            };
                        })
                        .collect(Collectors.toList());
            }
        }

        // ===================== Orden =====================
        if (sortedBy != null && !sortedBy.isEmpty()) {
            result.sort(Comparator.comparing(u -> {
                String val = getAttributeValue(u, sortedBy);
                return val != null ? val.toLowerCase().trim() : "";
            }));
        }

        // ===================== Eliminar contraseña =====================
        return result.stream()
                .map(this::removePassword)
                .collect(Collectors.toList());
    }

    private String getAttributeValue(User user, String attribute) {
        return switch (attribute) {
            case "id" -> user.getId().toString();
            case "email" -> user.getEmail();
            case "name" -> user.getName();
            case "phone" -> user.getPhone();
            case "tax_id" -> user.getTax_id();
            case "created_at" -> user.getCreated_at() != null ? user.getCreated_at().toString() : null;
            default -> null;
        };
    }

    public boolean deleteUser(UUID id) {
        return users.removeIf(u -> u.getId().equals(id));
    }

    public User createUser(User user) {
        // Validación RFC
        if (!validateTaxId(user.getTax_id())) {
            throw new IllegalArgumentException("Invalid tax_id format (RFC).");
        }

        // Verificar que el tax_id sea único
        if (!isTaxIdUnique(user.getTax_id(), null)) {
            throw new IllegalArgumentException("Tax_id must be unique.");
        }

        // Asignar ID y encriptar contraseña
        user.setId(UUID.randomUUID());
        user.setPassword(encryptPassword(user.getPassword()));
        user.setCreated_at(getCurrentTimestamp());

        // Asignar usuario a cada dirección
        if (user.getAddresses() != null) {
            for (Address addr : user.getAddresses()) {
                addr.setUser(user);
                addr.setId(null);
            }
        }

        // Agregar a la lista
        users.add(user);

        return removePassword(user);
    }

    public Optional<User> updateUser(UUID id, User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User existingUser = users.get(i);
            if (existingUser.getId().equals(id)) {
                // Actualizar campos si no son nulos
                if (updatedUser.getEmail() != null)
                    existingUser.setEmail(updatedUser.getEmail());
                if (updatedUser.getName() != null)
                    existingUser.setName(updatedUser.getName());
                if (updatedUser.getPhone() != null)
                    existingUser.setPhone(updatedUser.getPhone());

                // Actualizar tax_id si es válido y único
                if (updatedUser.getTax_id() != null && !updatedUser.getTax_id().equals(existingUser.getTax_id())) {
                    if (!isTaxIdUnique(updatedUser.getTax_id(), id)) {
                        throw new IllegalArgumentException("Tax_id update failed: must be unique.");
                    }
                    existingUser.setTax_id(updatedUser.getTax_id());
                }

                // Actualizar contraseña si se proporciona
                if (updatedUser.getPassword() != null) {
                    existingUser.setPassword(encryptPassword(updatedUser.getPassword()));
                }

                // Actualizar direcciones
                if (updatedUser.getAddresses() != null) {
                    for (Address addr : updatedUser.getAddresses()) {
                        addr.setUser(existingUser);
                        if (addr.getId() == null) {
                            addr.setId(null);
                        }
                    }
                    existingUser.setAddresses(updatedUser.getAddresses());
                }

                return Optional.of(removePassword(existingUser));
            }
        }
        return Optional.empty();
    }
}
