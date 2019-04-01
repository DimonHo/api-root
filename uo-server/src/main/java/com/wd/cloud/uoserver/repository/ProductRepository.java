package com.wd.cloud.uoserver.repository;

import com.wd.cloud.uoserver.pojo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
