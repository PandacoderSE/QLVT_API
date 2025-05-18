package DATN.ITDeviceManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import DATN.ITDeviceManagement.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    public List<Category> getCategoryByName(String name);
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Category checkExistName(String name);
    @Query("SELECT c FROM Category c ORDER BY c.createdTime DESC")
    public List<Category> findAllCategory();
}
