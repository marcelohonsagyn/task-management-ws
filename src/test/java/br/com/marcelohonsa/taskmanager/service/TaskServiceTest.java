package br.com.marcelohonsa.taskmanager.service;

import br.com.marcelohonsa.taskmanager.exception.ResourceNotFoundException;
import br.com.marcelohonsa.taskmanager.model.Task;
import br.com.marcelohonsa.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Task 1", "Description 1", false),
                new Task(2L, "Task 2", "Description 2", true)
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task(1L, "Task 1", "Description 1", false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Task 1", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(999L);
        });

        assertEquals("Task with ID 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void shouldCreateNewTask() {
        Task task = new Task(null, "New Task", "New Description", false);
        Task savedTask = new Task(1L, "New Task", "New Description", false);

        when(taskRepository.save(task)).thenReturn(savedTask);

        Task result = taskService.createTask(task);

        assertNotNull(result.getId());
        assertEquals("New Task", result.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void shouldUpdateExistingTask() {
        Task existingTask = new Task(1L, "Old Task", "Old Description", false);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Description", true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);

        Task result = taskService.updateTask(1L, updatedTask);

        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        Task updatedTask = new Task(999L, "Updated Task", "Updated Description", true);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(999L, updatedTask);
        });

        assertEquals("Task with ID 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void shouldDeleteTask() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        when(taskRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(999L);
        });

        assertEquals("Task with ID 999 not found", exception.getMessage());
        verify(taskRepository, times(1)).existsById(999L);
    }
}