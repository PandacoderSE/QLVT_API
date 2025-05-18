package DATN.ITDeviceManagement.controller;

import DATN.ITDeviceManagement.DTO.request.CategoryRequest;
import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import DATN.ITDeviceManagement.DTO.response.CategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import DATN.ITDeviceManagement.service.ICategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Qualifier("categoryServiceImpl")
    @Autowired
    private ICategoryService categoryService;
    @GetMapping("/all")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder().success(true).message("create successfully").data(categoryService.getAllCategory()).build();
    }
    @PostMapping("/add-new-category")
    public ApiResponse<?> addNewCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.addNewCategory(categoryRequest);
        return ApiResponse.builder().success(true).message("create successfully").data(categoryResponse).build();
    }
    @DeleteMapping("/{categoryId}")
    public ApiResponse<?> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.builder().success(true).message("delete successfully").data(null).build();
    }
    @PutMapping("/edit")
    public ApiResponse<?> editCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.editCategory(categoryRequest);
        return ApiResponse.builder().success(true).message("update successfully").data(categoryResponse).build();
    }
    @GetMapping("/update/{categoryId}")
    public ApiResponse<?> editCategory(@PathVariable("categoryId") Long categoryId) {
        CategoryResponse categoryResponse = categoryService.getDetailCategory(categoryId);
        return ApiResponse.builder().success(true).message("get detail successfully").data(categoryResponse).build();
    }
    @GetMapping("/search/{name}")
    public ApiResponse<List<CategoryResponse>> searchByCategoryName(@PathVariable String name) {
        List<CategoryResponse> categoryResponses = categoryService.searchByCategoryName(name);
        return ApiResponse.<List<CategoryResponse>>builder().success(true).message("search successfully").data(categoryResponses).build();
    }

}
