package com.WReport.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.WReport.model.SessionModel;

@Repository
public interface SessionModelRepository extends CrudRepository<SessionModel, Long>{

}
