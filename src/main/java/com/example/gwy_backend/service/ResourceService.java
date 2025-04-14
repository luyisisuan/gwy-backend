package com.example.gwy_backend.service;

import com.example.gwy_backend.entity.Resource;
import com.example.gwy_backend.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc();
    }

    public List<Resource> getResourcesByCategory(String category) {
        return resourceRepository.findByCategoryOrderByDisplayOrderAscCreatedAtDesc(category);
    }

    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public Resource createResource(Resource resource) {
        // 设置默认值
        if (resource.getIcon() == null || resource.getIcon().isEmpty()) {
            resource.setIcon("fas fa-link");
        }
        if (resource.getCategory() == null || resource.getCategory().isEmpty()) {
            resource.setCategory("其他");
        }
        if (resource.getDisplayOrder() == null) {
            resource.setDisplayOrder(0);
        }
        return resourceRepository.save(resource);
    }

    public Optional<Resource> updateResource(Long id, Resource resourceDetails) {
        return resourceRepository.findById(id)
                .map(existingResource -> {
                    existingResource.setTitle(resourceDetails.getTitle());
                    existingResource.setUrl(resourceDetails.getUrl());
                    
                    if (resourceDetails.getIcon() != null) {
                        existingResource.setIcon(resourceDetails.getIcon());
                    }
                    
                    if (resourceDetails.getCategory() != null) {
                        existingResource.setCategory(resourceDetails.getCategory());
                    }
                    
                    if (resourceDetails.getDescription() != null) {
                        existingResource.setDescription(resourceDetails.getDescription());
                    }
                    
                    if (resourceDetails.getDisplayOrder() != null) {
                        existingResource.setDisplayOrder(resourceDetails.getDisplayOrder());
                    }
                    
                    return resourceRepository.save(existingResource);
                });
    }

    public boolean deleteResource(Long id) {
        return resourceRepository.findById(id)
                .map(resource -> {
                    resourceRepository.delete(resource);
                    return true;
                })
                .orElse(false);
    }
}