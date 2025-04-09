// Base API URL
const API_URL = 'http://localhost:8080/api/products';
document.addEventListener("DOMContentLoaded", function() {
    loadProductsFromBackend();
    loadCategories();  // Load categories when page loads

    // Add these new event listeners right after loadProductsFromBackend()
    // 1. Search when Enter key is pressed
    document.getElementById("searchInput").addEventListener("keyup", function(e) {
        if (e.key === "Enter") {
            searchProducts();
        }
    });
    
    // 2. Search when category filter changes
    document.getElementById("categoryFilter").addEventListener("change", function() {
        searchProducts();
    });

    // If you want to implement debouncing (recommended), add this:
    let searchTimeout;
    function debounceSearch() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(searchProducts, 300);
    }
    
    // 3. Debounced search as you type (optional)
    document.getElementById("searchInput").addEventListener("keyup", debounceSearch);
});

// Toggle product form visibility
function toggleProductForm() {
    const form = document.getElementById("addProductForm");
    form.style.display = form.style.display === "none" ? "block" : "none";
    
    // Clear form when hiding
    if (form.style.display === "none") {
        clearForm();
    }
}

// Load products from backend API
async function loadProductsFromBackend() {
    try {
        const response = await fetch(API_URL);
        const responseText = await response.text();
        
        if (!response.ok) {
            throw new Error(responseText || 'Failed to fetch products');
        }
        
        const products = responseText ? JSON.parse(responseText) : [];
        displayProducts(products);
    } catch (error) {
        console.error('Error:', error);
        document.getElementById("productListing").innerHTML = `
            <p class="error-message">
                Error loading products: ${error.message}<br>
                ${await getBackendStatus()} <!-- Add this helper function -->
            </p>`;
    }
}
async function getBackendStatus() {
    try {
        const response = await fetch(API_URL);
        return `Backend status: ${response.status}`;
    } catch (e) {
        return 'Backend is unreachable';
    }
}
// Display multiple products
function displayProducts(products) {
    const productListing = document.getElementById("productListing");
    productListing.innerHTML = "";

    if (products.length === 0) {
        productListing.innerHTML = `<p>No products found.</p>`;
        return;
    }

    products.forEach(product => {
        // Handle image URL
        let imageUrl = product.imageUrl;
        if (imageUrl && !imageUrl.startsWith('http')) {
            imageUrl = `http://localhost:8080${imageUrl}`;
        }

        // Create product card HTML with all details
        const productHTML = `
            <div class="product-item" data-id="${product.productId}">
                <img src="${imageUrl || 'placeholder.jpg'}" 
                     alt="${product.productName}"
                     onerror="this.onerror=null; this.src='placeholder.jpg'">
                <div class="product-details">
                    <h3>${product.productName || 'No name'}</h3>
                    <p class="description">${product.description || 'No description available'}</p>
                    <p class="price">KES ${product.price?.toFixed(2) || '0.00'}</p>
                    <p class="category">Category: ${product.categoryName || 'Uncategorized'}</p>
                    <p class="condition">Condition: ${product.condition || 'Unknown'}</p>
                    <p class="status">Status: ${product.status || 'Available'}</p>
                    <button class="add-to-cart" onclick="addToCart(${product.productId})">
                        Add to Cart
                    </button>
                </div>
            </div>
        `;
        productListing.innerHTML += productHTML;
    });
}

// Add new product to backend

async function addProduct() {
    // Get form values
    const name = document.getElementById("productName").value.trim();
    const price = parseFloat(document.getElementById("productPrice").value);
    const category = document.getElementById("productCategory").value;
    const condition = document.getElementById("productCondition").value;
    
    // Validate required fields
    if (!name) {
        alert("Product name is required");
        return;
    }
    
    if (isNaN(price) || price <= 0) {
        alert("Please enter a valid price");
        return;
    }
    
    if (!category) {
        alert("Please select a category");
        return;
    }

    try {
        // Show loading state
        const productListing = document.getElementById("productListing");
        productListing.innerHTML = '<div class="loading-spinner"></div>';
        
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                productName: name,
                price: price,
                categoryName: category,
                condition: condition,
                description: document.getElementById("productDescription").value.trim() || null,
                imageUrl: await handleImageUpload() || null,
                userId: 1 // Temporary until auth is implemented
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || errorData.error || 'Failed to add product');
        }

        const result = await response.json();
        loadProductsFromBackend();
        toggleProductForm();
        clearForm();
    } catch (error) {
        console.error("Add product error:", error);
        document.getElementById("productListing").innerHTML = `
            <p class="error-message">Error: ${error.message}</p>`;
    }
}

// Handle image upload (simplified version)
// Replace the existing handleImageUpload() with this version
async function handleImageUpload() {
    const imageInput = document.getElementById("productImage");
    if (imageInput.files.length === 0) return null;
    
    try {
        // Show upload progress (optional)
        const productListing = document.getElementById("productListing");
        productListing.innerHTML = '<div class="loading-spinner">Uploading image...</div>';
        
        // Create FormData object
        const formData = new FormData();
        formData.append("file", imageInput.files[0]);
        
        // Send to backend
        const response = await fetch(`${API_URL}/upload`, {
            method: 'POST',
            body: formData
            // Don't set Content-Type header - the browser will do it automatically
        });
        
        if (!response.ok) {
            throw new Error(await response.text());
        }
        
        // Return the image URL from server
        return await response.text();
        
    } catch (error) {
        console.error("Image upload failed:", error);
        document.getElementById("productListing").innerHTML = `
            <p class="error-message">Image upload failed: ${error.message}</p>`;
        return null;
    }
}
// Enhanced search function
async function searchProducts() {
    const searchTerm = document.getElementById("searchInput").value.trim();
    const categoryFilter = document.getElementById("categoryFilter").value;

    try {
        // Show loading state
        const productListing = document.getElementById("productListing");
        productListing.innerHTML = '<div class="loading">Searching products...</div>';

        // Build query parameters
        const params = new URLSearchParams();
        if (searchTerm) params.append('name', searchTerm);
        if (categoryFilter && categoryFilter !== 'all') params.append('category', categoryFilter);

        const response = await fetch(`${API_URL}/search?${params.toString()}`);
        
        if (!response.ok) {
            throw new Error(await response.text() || 'Search failed');
        }
        
        const products = await response.json();
        displayProducts(products);
    } catch (error) {
        console.error('Search error:', error);
        productListing.innerHTML = `
            <p class="error">Search failed: ${error.message}</p>`;
    }
}

// Add event listeners for instant search
document.addEventListener("DOMContentLoaded", function() {
    // Existing code...
    
    // Enhanced search triggers
    document.getElementById("searchInput").addEventListener("input", debounceSearch);
    document.getElementById("categoryFilter").addEventListener("change", searchProducts);
});

// Better debounce function
let searchTimeout;
function debounceSearch() {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(searchProducts, 300);
}
// Add this function to fetch categories
async function loadCategories() {
    try {
        const response = await fetch(`${API_URL}/categories`);
        if (!response.ok) throw new Error('Failed to load categories');
        const categories = await response.json();
        
        const categorySelect = document.getElementById("productCategory");
        const filterSelect = document.getElementById("categoryFilter");
        
        // Clear existing options (keep first option)
        categorySelect.innerHTML = '<option value="">Select Category</option>';
        filterSelect.innerHTML = '<option value="all">All Categories</option>';
        
        // Add categories to both dropdowns
        categories.forEach(category => {
            const option = document.createElement("option");
            option.value = category.categoryName;
            option.textContent = category.categoryName;
            categorySelect.appendChild(option.cloneNode(true));
            filterSelect.appendChild(option);
        });
    } catch (error) {
        console.error("Error loading categories:", error);
    }
}
// Clear form fields
function clearForm() {
    document.getElementById("productName").value = "";
    document.getElementById("productDescription").value = "";
    document.getElementById("productPrice").value = "";
    document.getElementById("productCategory").value = "";
    document.getElementById("productCondition").value = "New";
    document.getElementById("productImage").value = "";
}
async function addToCart(productId) {
    try {
        const userId = 1; // Replace with actual user ID when you have auth
        const response = await fetch(`${API_URL}/cart/add?productId=${productId}&userId=${userId}`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        alert('Product added to cart!');
        updateCartCount();
    } catch (error) {
        console.error('Error adding to cart:', error);
        alert('Failed to add to cart: ' + error.message);
    }
}

// View Cart function
async function viewCart() {
    try {
        const userId = 1; // Replace with actual user ID
        const response = await fetch(`${API_URL}/cart/${userId}`);
        
        if (!response.ok) {
            throw new Error('Failed to load cart');
        }

        const cartItems = await response.json();
        displayCart(cartItems);
    } catch (error) {
        console.error('Error loading cart:', error);
    }
}

// Display Cart Items
function displayCart(cartItems) {
    const cartModal = document.createElement('div');
    cartModal.className = 'cart-modal';
    
    let total = 0;
    cartModal.innerHTML = `
        <div class="cart-content">
            <h2>Your Cart</h2>
            <button class="close-cart" onclick="this.parentElement.parentElement.remove()">×</button>
            <div class="cart-items">
                ${cartItems.map(item => {
                    total += item.subtotal;
                    // Handle image URL - prepend base URL if needed
                    let imageUrl = item.imageUrl;
                    if (imageUrl && !imageUrl.startsWith('http')) {
                        imageUrl = `http://localhost:8080${imageUrl}`;
                    }
                    
                    return `
                        <div class="cart-item">
                            <img src="${imageUrl || 'placeholder.jpg'}" 
                                 onerror="this.onerror=null; this.src='placeholder.jpg'">
                            <div class="cart-item-details">
                                <h3>${item.productName}</h3>
                                <p>Price: KES ${item.price.toFixed(2)}</p>
                                <p>Qty: ${item.quantity}</p>
                                <p>Subtotal: KES ${item.subtotal.toFixed(2)}</p>
                                <button class="remove-btn" 
                                        onclick="removeFromCart(${item.productId})">
                                    Remove
                                </button>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
            <div class="cart-total">
                <h3>Total: KES ${total.toFixed(2)}</h3>
                <button class="checkout-btn">Proceed to Checkout</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(cartModal);
}
//
async function removeFromCart(productId) {
    try {
        const userId = 1; // Replace with actual user ID when you have auth
        const response = await fetch(`${API_URL}/cart/remove?productId=${productId}&userId=${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        // Refresh the cart view
        viewCart();
        updateCartCount();
    } catch (error) {
        console.error('Error removing from cart:', error);
        alert('Failed to remove item: ' + error.message);
    }
}
// Update cart count in header
async function updateCartCount() {
    const userId = 1; // Replace with actual user ID
    const response = await fetch(`${API_URL}/cart/${userId}`);
    if (response.ok) {
        const cartItems = await response.json();
        const cartCount = cartItems.reduce((sum, item) => sum + item.quantity, 0);
        document.getElementById('cart-count').textContent = cartCount;
    }
}

// Add this to your DOMContentLoaded event
document.addEventListener("DOMContentLoaded", function() {
    // Existing code...
    
    // Add cart icon to header
    const header = document.querySelector('.header');
    header.innerHTML += `
        <div class="cart-icon" onclick="viewCart()">
            🛒 <span id="cart-count">0</span>
        </div>
    `;
    
    // Initial cart count update
    updateCartCount();
});