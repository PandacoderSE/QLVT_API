package DATN.ITDeviceManagement.service.impl;

import DATN.ITDeviceManagement.DTO.request.CategoryRequest;
import DATN.ITDeviceManagement.DTO.response.CategoryResponse;
import DATN.ITDeviceManagement.entity.Category;
import DATN.ITDeviceManagement.exception.ErrorCode;
import DATN.ITDeviceManagement.repository.CategoryRepository;
import DATN.ITDeviceManagement.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import DATN.ITDeviceManagement.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<CategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAllCategory();
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = convertToResponse(category);
            categoryResponses.add(categoryResponse);
        }
        return categoryResponses;
    }

    @Override
    public CategoryResponse addNewCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        Category categoryExisted = categoryRepository.checkExistName(categoryRequest.getName());
        if (categoryExisted != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.EXISTING_CATEGORY.getMessage());
        }
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        categoryRepository.save(category);
        return convertToResponse(category);
    }

    @Override
    public CategoryResponse editCategory(CategoryRequest categoryRequest) {
        Optional<Category> category = categoryRepository.findById(categoryRequest.getId());
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }
//        Category categoryExisted = categoryRepository.checkExistName(categoryRequest.getName());
////        if (categoryExisted.getName().equals(categoryRequest.getName())) {
////            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.EXISTING_CATEGORY.getMessage());
////        }
        category.get().setName(categoryRequest.getName());
        category.get().setDescription(categoryRequest.getDescription());
        categoryRepository.save(category.get());
        return convertToResponse(category.get());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }
        deviceRepository.updateCategoryIdToNull(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryResponse getDetailCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_CATEGORY.getMessage());
        }
        return convertToResponse(category.get());
    }

    @Override
    public List<CategoryResponse> searchByCategoryName(String categoryName) {
        List<Category> categories = categoryRepository.getCategoryByName(categoryName);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponse categoryResponse = convertToResponse(category);
            categoryResponses.add(categoryResponse);
        }
        return categoryResponses;
    }

    public CategoryResponse convertToResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setDescription(category.getDescription());
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        return categoryResponse;
    }
}
