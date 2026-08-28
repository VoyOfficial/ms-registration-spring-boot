package br.voy.application.util;

import org.springframework.stereotype.Component;

/**
 * Utility class to get the current authenticated user TODO: Implement with Spring Security when
 * authentication is added
 */
@Component
public class CurrentUserHelper {

    /**
     * Get the current authenticated user ID
     *
     * @return user ID if authenticated, null if not authenticated
     */
    public Long getCurrentUserId() {
        // TODO: Implement with Spring Security
        // Example:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated() && !(authentication
        // instanceof AnonymousAuthenticationToken)) {
        //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //     return userDetails.getId();
        // }

        // For now, return null (user not logged in)
        return null;
    }

    /**
     * Check if there is a user currently authenticated
     *
     * @return true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        return getCurrentUserId() != null;
    }
}
