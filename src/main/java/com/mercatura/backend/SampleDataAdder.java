package com.mercatura.backend;

import com.mercatura.backend.entity.*;
import com.mercatura.backend.repository.*;
import com.mercatura.backend.service.ImageService;
import com.mercatura.backend.utils.SampleImageUploader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component
public class SampleDataAdder {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public SampleDataAdder(ProductCategoryRepository productCategoryRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           ReviewRepository reviewRepository,
                           CartRepository cartRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           ImageRepository imageRepository,
                           ImageService imageService
    ) {
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
    }

    @Bean
    CommandLineRunner addSampleData() {
        return args -> {
            // Roles
            addRole("USER");
            addRole("MERCHANDISER");
            addRole("ADMIN");

            // Categories
            ProductCategory electronics = addCategory("Electronics");
            ProductCategory homeAppliances = addCategory("Home Appliances");
            ProductCategory fashion = addCategory("Fashion");
            ProductCategory hygienic = addCategory("Hygienic");
            ProductCategory software = addCategory("Software");

            // Products
            // Electronics
            Product phone = addProduct("Phone", "Experience cutting-edge technology with our sleek" +
                            " smartphone, featuring a vibrant display, high-resolution camera, and long-lasting battery."
                            + " Perfect for staying connected and capturing life's moments.",
                    699.99, electronics);
            Product tablet = addProduct("Tablet", "Experience cutting-edge technology with our sleek" +
                            " tablet, featuring a vibrant display, high-resolution camera, and long-lasting battery." +
                            " Perfect for staying connected and capturing life's moments.",
                    499.99, electronics);

            // Home Appliances
            Product toaster = addProduct("Toaster", "Just what you need", 99.89, homeAppliances);
            Product kettle = addProduct("Kettle", "Will make your water boiling hot",
                    39.99, homeAppliances);

            // Fashion
            Product dress = addProduct("Dress", "May be a nice gift", 38.99, fashion);
            Product socks = addProduct("Socks", "Just what you expect", 4.99, fashion);

            // Hygienic
            Product soap = addProduct("Soap", "Cleaning is a human nature", 2.99, hygienic);
            Product shampoo = addProduct("Shampoo", "Cleaning is a human nature", 4.99, hygienic);

            // Software
            Product windows11 = addProduct("Windows 11", "Cheaper than original, more expensive than pirating.",
                    19.99, software);
            Product ubuntu = addProduct("Ubuntu", "In any case, more expensive", 2.99, software);

            addImageToProduct(phone, "phone.jpeg");
            addImageToProduct(tablet, "tablet.jpeg");
            addImageToProduct(toaster, "toaster-2.jpeg");
            addImageToProduct(toaster, "toaster-3.jpeg");
            addImageToProduct(toaster, "toaster-4.jpeg");
            addImageToProduct(kettle, "kettle.jpeg");
            addImageToProduct(dress, "dress.jpeg");
            addImageToProduct(socks, "socks.jpeg");
            addImageToProduct(soap, "soap.jpeg");
            addImageToProduct(shampoo, "shampoo.jpeg");
            addImageToProduct(windows11, "windows11.jpeg");
            addImageToProduct(ubuntu, "ubuntu.jpeg");


            // Users
            ApplicationUser regular = addUser("Nijat", "Kazimli",
                    "nijatkazimli", "nijatkazimli", "USER");
            ApplicationUser merchandiser = addUser("Nijat", "Merchandiser",
                    "nijatmerchandiser", "nijatmerchandiser", "MERCHANDISER");
            addUser("admin", "admin", "admin", "password", "ADMIN");
            addImageToUser(regular, "user.jpeg");
            addImageToUser(merchandiser, "merchandiser.jpeg");


            // Reviews
            addReview("Good", 4, regular, phone);
            addReview("Excellent", 5, merchandiser, phone);
            addReview("Good", 4, regular, tablet);
            addReview("Excellent", 5, merchandiser, tablet);
            addReview("Good", 4, regular, toaster);
            addReview("Excellent", 5, merchandiser, kettle);
            addReview("Good", 4, regular, dress);
            addReview("Good", 5, merchandiser, dress);
            addReview("Very Bad", 1, regular, socks);
            addReview("Bad", 2, merchandiser, socks);
            addReview("What can you expect?", 4, regular, soap);
            addReview("It does what it should do", 4, merchandiser, soap);
            addReview("It did not grow any hair!", 1, regular, shampoo);
            addReview("I am still bald", 2, merchandiser, shampoo);
            addReview("Slow", 2, regular, windows11);
            addReview("Updates everyday", 1, merchandiser, windows11);
            addReview("Open-source but costs money here", 2, regular, ubuntu);
            addReview("I love penguins", 5, merchandiser, ubuntu);


            // Carts
            if (windows11 != null && socks != null && kettle != null) {
                addCart(regular, false, List.of(windows11, socks, kettle));
            }
            if (toaster != null && phone != null && soap != null) {
                addCart(merchandiser, true, List.of(toaster, phone, soap));
            }

        };
    }

    private void addRole(String authority) {
        if (roleRepository.findByAuthority(authority).isEmpty()) {
            roleRepository.save(new Role(authority));
        }
    }

    private ProductCategory addCategory(String categoryName) {
        if (productCategoryRepository.findByName(categoryName).isEmpty()) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setName(categoryName);
            return productCategoryRepository.save(productCategory);
        }
        return null;
    }

    private Product addProduct(String name, String description, Double price, ProductCategory category) {
        if (productRepository.findByName(name).isEmpty() && category != null) {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            return productRepository.save(product);
        }
        return null;
    }

    private ApplicationUser addUser(String name, String surname, String username, String password, String role) {
        if (userRepository.findByUsername(username).isEmpty()) {
            String encoded = passwordEncoder.encode(password);
            Set<Role> userRoles = new HashSet<>();
            Optional<Role> userRole = roleRepository.findByAuthority(role);
            if (userRole.isPresent()) {
                userRoles.add(userRole.get());
                ApplicationUser user = new ApplicationUser(
                        name,
                        surname,
                        username,
                        encoded,
                        userRoles
                );
                return userRepository.save(user);
            }
        }
        return null;
    }

    private void addReview(String content, Integer rating, ApplicationUser author, Product product) {
        if (author != null && product != null) {
            Review review = new Review();
            review.setContent(content);
            review.setRating(rating);
            review.setAuthor(author);
            review.setProduct(product);
            Double ratingSum = Double.valueOf(rating);
            int reviews = 1;
            if (product.getReviews() != null) {
                reviews = product.getReviews().size();
                ratingSum = product.getReviews().stream().mapToDouble(Review::getRating).sum();
            }
            product.setRating(ratingSum / (double) reviews);
            productRepository.save(product);
            reviewRepository.save(review);
        }
    }

    private void addCart(ApplicationUser user, boolean isPaid, List<Product> products) {
        if (user != null && products != null) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setPaid(isPaid);
            Set<Product> productSet = new HashSet<>(products);
            cart.setProducts(productSet);
            cartRepository.save(cart);
        }
    }

    private void addImageToUser(ApplicationUser user, String fileName) throws IOException {
        ClassLoader classLoader = BackendApplication.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream("sampleImages/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + fileName);
            }

            Path tempFile = Files.createTempFile("sampleProfile", ".jpeg");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            if (user != null && imageService != null) {
                String url = SampleImageUploader.uploadSingleImage(imageService, tempFile.toFile());
                Image image = new Image();
                image.setImageUrl(url);
                image.setUser(user);
                user.setProfileImage(image);
                imageRepository.save(image);
                userRepository.save(user);
            }
            Files.deleteIfExists(tempFile);
        }
    }

    private void addImageToProduct(Product product, String fileName) throws IOException {
        ClassLoader classLoader = BackendApplication.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream("sampleImages/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("File not found: " + fileName);
            }

            Path tempFile = Files.createTempFile("sampleProduct", ".jpeg");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);

            if (product != null && imageService != null) {
                String url = SampleImageUploader.uploadSingleImage(imageService, tempFile.toFile());
                Image image = new Image();
                image.setProduct(product);
                image.setImageUrl(url);
                List<Image> productImages = product.getImages();

                if (productImages == null) {
                    productImages = new ArrayList<>();
                    product.setImages(productImages);
                }
                productImages.add(image);
                imageRepository.save(image);
                productRepository.save(product);
            }
            Files.deleteIfExists(tempFile);
        }
    }
}
