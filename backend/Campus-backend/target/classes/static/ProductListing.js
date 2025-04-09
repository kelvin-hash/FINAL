document.addEventListener("DOMContentLoaded", function () {
    loadProductsFromStorage();
});

/**
 * Toggle the visibility of the Add Product form
 */
function toggleProductForm() {
    const form = document.getElementById("addProductForm");
    form.style.display = form.style.display === "none" || form.style.display === "" ? "block" : "none";
}

/**
 * Add a new product dynamically to the product listing
 */
function addProduct() {
    let name = document.getElementById("productName").value.trim();
    let category = document.getElementById("productCategory").value.trim();
    let price = document.getElementById("productPrice").value.trim();
    let imageInput = document.getElementById("productImage");

    if (!name || !category || !price || imageInput.files.length === 0) {
        alert("Please fill in all fields.");
        return;
    }

    let imageUrl = URL.createObjectURL(imageInput.files[0]);

    let newProduct = { name, category, price, imageUrl };

    displayProduct(newProduct);
    saveProductToStorage(newProduct);

    // Clear input fields after adding product
    document.getElementById("productName").value = "";
    document.getElementById("productCategory").value = "";
    document.getElementById("productPrice").value = "";
    document.getElementById("productImage").value = "";
}

/**
 * Display a single product in the product listing section
 */
function displayProduct(product) {
    let productListing = document.getElementById("productListing");

    let productHTML = `
        <div class="product-item">
            <img src="${product.imageUrl}" alt="${product.name}">
            <h3>${product.name}</h3>
            <p>Category: ${product.category}</p>
            <p class="price">$${product.price}</p>
        </div>
    `;

    productListing.innerHTML += productHTML;
}

/**
 * Save the product to localStorage
 */
function saveProductToStorage(product) {
    let products = JSON.parse(localStorage.getItem("products")) || [];
    products.push(product);
    localStorage.setItem("products", JSON.stringify(products));
}

/**
 * Load products from localStorage when the page loads
 */
function loadProductsFromStorage() {
    let products = JSON.parse(localStorage.getItem("products")) || [];
    let productListing = document.getElementById("productListing");

    productListing.innerHTML = ""; // Clear previous products before loading

    products.forEach(product => {
        displayProduct(product);
    });
}

/**
 * Search function to filter products based on input
 */
function searchProducts() {
    let searchInput = document.getElementById("searchInput").value.toLowerCase();
    let categoryFilter = document.getElementById("categoryFilter").value.toLowerCase();
    let products = JSON.parse(localStorage.getItem("products")) || [];
    let productListing = document.getElementById("productListing");

    productListing.innerHTML = ""; // Clear current display

    let filteredProducts = products.filter(product => {
        return (product.name.toLowerCase().includes(searchInput) || product.category.toLowerCase().includes(searchInput)) &&
               (categoryFilter === "all" || product.category.toLowerCase() === categoryFilter);
    });

    if (filteredProducts.length === 0) {
        productListing.innerHTML = "<p>No products found.</p>";
    } else {
        filteredProducts.forEach(product => displayProduct(product));
    }
}
