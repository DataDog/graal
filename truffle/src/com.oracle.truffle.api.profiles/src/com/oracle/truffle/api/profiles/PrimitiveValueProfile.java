/*
 * Copyright (c) 2014, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.api.profiles;

import java.util.Objects;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.NeverDefault;

/**
 * <p>
 * Represents a {@link ValueProfile} that speculates on the primitive equality or object identity of
 * values. Note that for {@code float} and {@code double} values we compare primitive equality via
 * {@link Float#floatToRawIntBits} and {@link Double#doubleToRawLongBits}, so that for example
 * {@code -0.0} is not considered the same as {@code 0.0}, even though primitive equality would
 * normally say that it was.
 * </p>
 *
 * {@inheritDoc}
 *
 * @since 0.10
 */
public final class PrimitiveValueProfile extends ValueProfile {

    private static final PrimitiveValueProfile DISABLED;
    static {
        PrimitiveValueProfile profile = new PrimitiveValueProfile();
        profile.disable();
        DISABLED = profile;
    }

    private static final Object UNINITIALIZED = new Object();
    private static final Object GENERIC = new Object();

    // Use only one field for thread safety.
    @CompilationFinal private Object cachedValue = UNINITIALIZED;

    PrimitiveValueProfile() {
    }

    /** @since 0.8 or earlier */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T profile(T v) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            Object value = v;
            if (snapshot instanceof Byte) {
                if (value instanceof Byte && (byte) snapshot == (byte) value) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Short) {
                if (value instanceof Short && (short) snapshot == (short) value) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Integer) {
                if (value instanceof Integer && (int) snapshot == (int) value) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Long) {
                if (value instanceof Long && (long) snapshot == (long) value) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Float) {
                /*
                 * -0.0 == 0.0, but you can tell the difference through other means, so we need to
                 * differentiate.
                 */
                if (value instanceof Float && Float.floatToRawIntBits((float) snapshot) == Float.floatToRawIntBits((float) value)) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Double) {
                /*
                 * -0.0 == 0.0, but you can tell the difference through other means, so we need to
                 * differentiate.
                 */
                if (value instanceof Double && Double.doubleToRawLongBits((double) snapshot) == Double.doubleToRawLongBits((double) value)) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Boolean) {
                if (value instanceof Boolean && (boolean) snapshot == (boolean) value) {
                    return (T) snapshot;
                }
            } else if (snapshot instanceof Character) {
                if (value instanceof Character && (char) snapshot == (char) value) {
                    return (T) snapshot;
                }
            } else if (snapshot == value) {
                return (T) snapshot;
            }
            CompilerDirectives.transferToInterpreterAndInvalidate();
            slowPath(value);
        }
        return v;
    }

    /** @since 0.8 or earlier */
    public byte profile(byte value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Byte && (byte) snapshot == value) {
                return (byte) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public short profile(short value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Short && (short) snapshot == value) {
                return (short) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public int profile(int value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Integer && (int) snapshot == value) {
                return (int) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public long profile(long value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Long && (long) snapshot == value) {
                return (long) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public float profile(float value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            /*
             * -0.0 == 0.0, but you can tell the difference through other means, so we need to
             * differentiate.
             */
            if (snapshot instanceof Float && Float.floatToRawIntBits((float) snapshot) == Float.floatToRawIntBits(value)) {
                return (float) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public double profile(double value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            /*
             * -0.0 == 0.0, but you can tell the difference through other means, so we need to
             * differentiate.
             */
            if (snapshot instanceof Double && Double.doubleToRawLongBits((double) snapshot) == Double.doubleToRawLongBits(value)) {
                return (double) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public boolean profile(boolean value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Boolean && (boolean) snapshot == value) {
                return (boolean) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /** @since 0.8 or earlier */
    public char profile(char value) {
        Object snapshot = this.cachedValue;
        if (snapshot != GENERIC) {
            if (snapshot instanceof Character && (char) snapshot == value) {
                return (char) snapshot;
            } else {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                slowPath(value);
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * @since 22.1
     */
    @Override
    public void disable() {
        this.cachedValue = GENERIC;
    }

    /**
     * {@inheritDoc}
     *
     * @since 22.1
     */
    @Override
    public void reset() {
        if (this != DISABLED) {
            this.cachedValue = UNINITIALIZED;
        }
    }

    private void slowPath(Object value) {
        if (cachedValue == UNINITIALIZED) {
            cachedValue = value;
        } else {
            cachedValue = GENERIC;
        }
    }

    boolean isGeneric() {
        return cachedValue == GENERIC;
    }

    boolean isUninitialized() {
        return cachedValue == UNINITIALIZED;
    }

    Object getCachedValue() {
        return cachedValue;
    }

    /**
     * {@inheritDoc}
     *
     * @since 22.1
     */
    @Override
    public String toString() {
        if (this == DISABLED) {
            return toStringDisabled();
        } else {
            return toString(PrimitiveValueProfile.class, isUninitialized(), isGeneric(), formatSpecialization());
        }
    }

    private String formatSpecialization() {
        if (!isUninitialized() && !isGeneric()) {
            Object snapshot = this.cachedValue;
            if (snapshot == null) {
                return String.format("value == null");
            } else {
                if (snapshot instanceof Byte || snapshot instanceof Short || snapshot instanceof Integer || snapshot instanceof Long || snapshot instanceof Float || snapshot instanceof Double ||
                                snapshot instanceof Boolean || snapshot instanceof Character) {
                    return String.format("value == (%s)%s", snapshot.getClass().getSimpleName(), snapshot);
                } else {
                    String simpleName = snapshot.getClass().getSimpleName();
                    return String.format("value == %s@%x", simpleName, Objects.hash(snapshot));
                }
            }
        }
        return null;
    }

    /**
     * Returns a {@link PrimitiveValueProfile} that speculates on the primitive equality or object
     * identity of a value.
     *
     * @since 0.10
     */
    @SuppressWarnings("deprecation")
    @NeverDefault
    public static PrimitiveValueProfile createEqualityProfile() {
        return create();
    }

    /**
     * Returns a {@link PrimitiveValueProfile} that speculates on the primitive equality or object
     * identity of a value.
     *
     * @since 22.1
     */
    @SuppressWarnings("deprecation")
    @NeverDefault
    public static PrimitiveValueProfile create() {
        if (Profile.isProfilingEnabled()) {
            return new PrimitiveValueProfile();
        } else {
            return DISABLED;
        }
    }

    /**
     * Returns the uncached version of the profile. The uncached version of a profile does nothing.
     *
     * @since 19.0
     */
    public static PrimitiveValueProfile getUncached() {
        return DISABLED;
    }

}
