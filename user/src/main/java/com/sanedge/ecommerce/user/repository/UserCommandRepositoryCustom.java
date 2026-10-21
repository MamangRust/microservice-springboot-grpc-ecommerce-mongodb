package com.sanedge.ecommerce.user.repository;

import com.sanedge.ecommerce.user.models.User;

public interface UserCommandRepositoryCustom {
    User trashed(String userId);

    User restore(String userId);

    int deletePermanent(String userId);

    int restoreAllDeleted();

    int deleteAllDeleted();
}
