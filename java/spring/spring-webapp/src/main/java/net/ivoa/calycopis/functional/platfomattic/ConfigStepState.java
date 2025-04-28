/**
 * 
 */
package net.ivoa.calycopis.functional.platfomattic;

/**
 * Enumeration of states for ConfigSteps.
 * 
 */
public enum ConfigStepState
    {
    
    PENDING(),
    ALLOCATING(),
    READY(),
    RELEASING(),
    COMPLETED(),
    CANCELLED(),
    FAILED();
    
    }
