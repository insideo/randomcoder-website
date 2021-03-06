package org.randomcoder.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

/**
 * Role repository.
 */
@Repository public interface RoleRepository extends JpaRepository<Role, Long> {
  /**
   * Finds a {@code Role} instance with the given name.
   *
   * @param name role name
   * @return {@code Role} instance, or null if not found
   */
  @Query("from Role r where r.name = ?1")
  @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
  public Role findByName(String name);
}