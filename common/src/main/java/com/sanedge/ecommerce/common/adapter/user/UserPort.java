package com.sanedge.ecommerce.common.adapter.user;

import com.sanedge.ecommerce.common.adapter.model.User;
import com.sanedge.ecommerce.common.adapter.model.VerifyPasswordResult;

/**
 * Port user: baca + tulis (atas nama auth). Setara
 * {@code user.QueryRepository}/{@code CommandRepository} di Go.
 */
public interface UserPort {

    /** @throws com.sanedge.ecommerce.common.exception.ResourceNotFoundException bila tidak ada. */
    User findById(int id);

    /** Termasuk password hash, untuk verifikasi kredensial. */
    User findByEmail(String email);

    /** Alias {@link #findByEmail(String)} (perilaku login lama). */
    User findByEmailAndVerify(String email);

    User findByVerificationCode(String code);

    User createUser(RegisterData data);

    /** Verifikasi kredensial login; {@code valid=false} berarti password salah. */
    VerifyPasswordResult verifyPassword(String email, String password);

    User updateIsVerified(int userId, boolean isVerified);

    User updatePassword(int userId, String password);

    void deleteUserPermanent(int userId);

    record RegisterData(
            String firstname,
            String lastname,
            String email,
            String password,
            String confirmPassword) {
    }
}
