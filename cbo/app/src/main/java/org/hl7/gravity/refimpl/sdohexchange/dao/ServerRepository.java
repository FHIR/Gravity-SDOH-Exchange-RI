package org.hl7.gravity.refimpl.sdohexchange.dao;

import org.hl7.gravity.refimpl.sdohexchange.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer> {

  Optional<Server> findByServerName(String serverName);

  // Additional delete method to return row count
  long deleteServerById(Integer id);
}
