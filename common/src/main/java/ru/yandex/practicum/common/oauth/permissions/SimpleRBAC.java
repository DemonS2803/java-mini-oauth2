package ru.yandex.practicum.common.oauth.permissions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleRBAC {

    public static final String VIEWER_ROLE = "viewer";
    public static final String EDITOR_ROLE = "editor";

    public static final String READ_SCOPE = ":read";
    public static final String EDIT_SCOPE = ":edit";

    public static boolean isValidPermissions(List<String> roles, List<String> scopes) {
        Set<String> rolesSet = new HashSet<>(roles);

        for (String scope : scopes) {
            if (!rolesSet.contains(mapScopeRole(scope))) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Map scopes to their roles, otherwise null
     *  payment:read -> viewer
     */
    private static String mapScopeRole(String scope) {
        int idx = scope.indexOf(":");
        scope = scope.substring(idx);
        return switch (scope) {
            case READ_SCOPE -> VIEWER_ROLE;
            case EDIT_SCOPE -> EDITOR_ROLE;
            default -> null;
        };
    }

    public static List<String> getMinimalRolesList(List<String> scopes) {
        Set<String> roles = new HashSet<>();
        scopes.forEach(scope -> roles.add(mapScopeRole(scope)));
        return new ArrayList<>(roles);
    }

}
