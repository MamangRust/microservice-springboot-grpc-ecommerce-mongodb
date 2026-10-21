package com.sanedge.ecommerce.banner.repository;

import com.sanedge.ecommerce.banner.models.Banner;

public interface BannerCommandRepositoryCustom {
    Banner trashed(String bannerId);

    Banner restore(String bannerId);

    boolean deletePermanent(String bannerId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
