package com.wd.cloud.docdelivery.repository;

import com.wd.cloud.docdelivery.entity.VLiterature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VLiteratureRepository extends JpaRepository<VLiterature, Long>, JpaSpecificationExecutor<VLiterature> {
}
