package com.example.gwy_backend.controller;

import com.example.gwy_backend.entity.Resource;
import com.example.gwy_backend.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.bind.annotation.CrossOrigin; // <-- 移除这个 import

import java.util.List;

@RestController
@RequestMapping("/api/resources")
// @CrossOrigin(origins = "*") // <-- 移除这个注解
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<Resource> getAllResources() {
        // 添加日志以确认方法被调用
        System.out.println(">>> 进入 ResourceController.getAllResources() 方法");
        try {
            List<Resource> resources = resourceService.getAllResources();
            System.out.println(">>> 从 Service 获取到 " + (resources != null ? resources.size() : "null") + " 条资源数据");
            return resources;
        } catch (Exception e) {
            // 捕获并打印来自 Service 层的任何异常
            System.err.println("### ResourceController 获取资源时出错: " + e.getMessage());
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            // 考虑重新抛出异常或返回错误响应
            throw e; // 或者返回 ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/category/{category}")
    public List<Resource> getResourcesByCategory(@PathVariable String category) {
        System.out.println(">>> 进入 ResourceController.getResourcesByCategory(), category=" + category);
        return resourceService.getResourcesByCategory(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
         System.out.println(">>> 进入 ResourceController.getResourceById(), id=" + id);
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
         System.out.println(">>> 进入 ResourceController.createResource()");
        Resource createdResource = resourceService.createResource(resource);
        return ResponseEntity.ok(createdResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(@PathVariable Long id, @RequestBody Resource resourceDetails) {
        System.out.println(">>> 进入 ResourceController.updateResource(), id=" + id);
        return resourceService.updateResource(id, resourceDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        System.out.println(">>> 进入 ResourceController.deleteResource(), id=" + id);
        boolean deleted = resourceService.deleteResource(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}