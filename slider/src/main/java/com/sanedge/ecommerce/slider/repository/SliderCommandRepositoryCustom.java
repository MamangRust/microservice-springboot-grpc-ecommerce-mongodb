package com.sanedge.ecommerce.slider.repository;

import com.sanedge.ecommerce.slider.models.Slider;

public interface SliderCommandRepositoryCustom {
    Slider trashed(String sliderId);

    Slider restore(String sliderId);

    boolean deletePermanent(String sliderId);

    boolean restoreAllDeleted();

    boolean deleteAllDeleted();
}
