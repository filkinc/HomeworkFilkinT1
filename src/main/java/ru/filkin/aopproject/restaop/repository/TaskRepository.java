package ru.filkin.aopproject.restaop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.filkin.aopproject.restaop.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
}
