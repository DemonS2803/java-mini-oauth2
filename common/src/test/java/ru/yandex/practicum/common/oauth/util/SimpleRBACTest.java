package ru.yandex.practicum.common.oauth.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.common.oauth.permissions.SimpleRBAC;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleRBACTest {

    @ParameterizedTest
    @MethodSource
    void testRBACValidator_shouldPass(List<String> roles, List<String> scopes) {
        assertTrue(SimpleRBAC.isValidPermissions(roles, scopes));
    }

    @ParameterizedTest
    @MethodSource
    void testRBACValidator_shouldFail(List<String> roles, List<String> scopes) {
        assertFalse(SimpleRBAC.isValidPermissions(roles, scopes));
    }

    public static Stream<Arguments> testRBACValidator_shouldPass() {
        return Stream.of(
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE), List.of()),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE))
        );
    }

    public static Stream<Arguments> testRBACValidator_shouldFail() {
        return Stream.of(
                Arguments.of(List.of(), List.of("payments" + SimpleRBAC.READ_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE), List.of("shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE)),
                Arguments.of(List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "payments:approve")),
                Arguments.of(List.of(SimpleRBAC.EDITOR_ROLE), List.of("payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE, "shop" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testRBAC_minimalRolesList(List<String> scopes, List<String> expectedRoles) {
        List<String> roles = SimpleRBAC.getMinimalRolesList(scopes);
        assertThat(expectedRoles).containsAll(roles);
    }

    public static Stream<Arguments> testRBAC_minimalRolesList() {
        return Stream.of(
                Arguments.of(List.of(), List.of()),
                Arguments.of(List.of("payments" + SimpleRBAC.READ_SCOPE), List.of(SimpleRBAC.VIEWER_ROLE)),
                Arguments.of(List.of("payments" + SimpleRBAC.EDIT_SCOPE), List.of(SimpleRBAC.EDITOR_ROLE)),
                Arguments.of(List.of("payments" + SimpleRBAC.READ_SCOPE, "payments" + SimpleRBAC.READ_SCOPE), List.of(SimpleRBAC.VIEWER_ROLE)),
                Arguments.of(List.of("payments" + SimpleRBAC.READ_SCOPE, "payments" + SimpleRBAC.EDIT_SCOPE), List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE))
        );
    }

}
