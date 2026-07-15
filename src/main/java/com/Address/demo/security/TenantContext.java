package com.Address.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {

    private static final Logger logger =
            LoggerFactory.getLogger(TenantContext.class);

    private static final ThreadLocal<String> currentTenant =
            new ThreadLocal<>();

    private static final ThreadLocal<String> currentTenantName =
            new ThreadLocal<>();

    // ❌ Default tenant removed
    private static final String DEFAULT_TENANT = null;

    // Set tenant ID
    public static void setTenant(String tenant) {

        if (tenant == null || tenant.trim().isEmpty()) {
            logger.warn("Tenant is null or empty");
            return;
        }

        logger.debug("Setting tenant context: {}", tenant);
        currentTenant.set(tenant);
    }

    // Get current tenant ID
    public static String getTenant() {
        return currentTenant.get();
    }

    // Set tenant with name
    public static void setTenantWithDetails(
            String tenant,
            String tenantName) {

        setTenant(tenant);

        if (tenantName != null) {
            currentTenantName.set(tenantName);
        }
    }

    // Get tenant name
    public static String getTenantName() {
        return currentTenantName.get();
    }

    // Check if tenant exists
    public static boolean isTenantSet() {
        return currentTenant.get() != null;
    }

    // Clear context
    public static void clear() {

        logger.debug("Clearing tenant context");

        currentTenant.remove();
        currentTenantName.remove();
    }

    // Required tenant
    public static String getRequiredTenant() {

        String tenant = currentTenant.get();

        if (tenant == null) {
            throw new IllegalStateException(
                    "Tenant Missing. Please login again."
            );
        }

        return tenant;
    }
}