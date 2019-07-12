package com.github.roookeee.datus.shared;

/**
 * A SafetyMode configures which additional implicit safety checks datus mapping steps employ.
 * This is an internal class and thus should not be used directly.
 */
public enum SafetyMode {
    /**
     * (default) No additional safety checks are employed.
     */
    NONE,
    /**
     * Any mapping steps are implicitly checked for null inputs before being applied.
     */
    NULL_SAFE
}
