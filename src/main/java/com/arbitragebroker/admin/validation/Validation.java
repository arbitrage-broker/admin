package com.arbitragebroker.admin.validation;

/**
 * @author Behrooz Mohamadi
 * @email behroooz.mohamadi@gmail.com
 * @date 9/1/2018
 */
public interface Validation<T> {
    void validate(T t, Object... args);
}
