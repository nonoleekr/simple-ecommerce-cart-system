import type { Cart, CatalogCategory, Order, Product, ProductReviews, Review, SellerOrder, Session } from "./types";

type ApiProduct = {
  id: string;
  name: string;
  slug: string;
  price: number;
  stockQuantity: number;
  sellerName: string;
  description: string;
  status: Product["status"];
  category: { name: Product["category"] };
};

type ProductPage = { content: ApiProduct[] };

const productImages: Record<string, string> = {
  "cafe-headphones": "/assets/headphones.png",
  "mechanical-keyboard": "/assets/keyboard.png",
  "linen-weekender": "/assets/weekender.png",
  "ceramic-table-lamp": "/assets/lamp.png"
};

const mapProduct = (product: ApiProduct): Product => ({
  ...product,
  category: product.category.name,
  image: productImages[product.slug] ?? "/assets/storefront-hero.png"
});

export async function fetchProducts(signal: AbortSignal): Promise<Product[]> {
  const response = await fetch("/api/v1/products?size=20", { signal });
  if (!response.ok) throw new Error("Unable to load products");
  const page = (await response.json()) as ProductPage;
  return page.content.map(mapProduct);
}

async function request<T>(path: string, options: RequestInit = {}, token?: string): Promise<T> {
  const response = await fetch(path, {
    ...options,
    headers: { "Content-Type": "application/json", ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers }
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({})) as { message?: string; detail?: string };
    throw new Error(body.message ?? body.detail ?? "Something went wrong. Please try again.");
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const login = (identifier: string, password: string) => request<Session>("/api/v1/auth/login", { method: "POST", body: JSON.stringify({ identifier, password }) });
export const register = (input: { username: string; email: string; password: string; displayName: string }) => request<void>("/api/v1/auth/register", { method: "POST", body: JSON.stringify(input) });
export const fetchCart = (token: string) => request<Cart>("/api/v1/cart", {}, token);
export const addCartItem = (token: string, productId: string) => request<Cart>("/api/v1/cart/items", { method: "POST", body: JSON.stringify({ productId, quantity: 1 }) }, token);
export const updateCartItem = (token: string, productId: string, quantity: number) => request<Cart>(`/api/v1/cart/items/${productId}`, { method: "PATCH", body: JSON.stringify({ quantity }) }, token);
export const removeCartItem = (token: string, productId: string) => request<Cart>(`/api/v1/cart/items/${productId}`, { method: "DELETE" }, token);
export const checkout = (token: string, input: { recipientName: string; addressLine1: string; city: string; postalCode: string; country: string }) => request<Order>("/api/v1/orders/checkout", { method: "POST", body: JSON.stringify(input) }, token);
export const fetchOrders = (token: string) => request<Order[]>("/api/v1/orders", {}, token);
export async function fetchWishlist(token: string): Promise<Product[]> {
  const products = await request<ApiProduct[]>("/api/v1/wishlist", {}, token);
  return products.map(mapProduct);
}
export const saveWishlistItem = (token: string, productId: string) => request<void>(`/api/v1/wishlist/items/${productId}`, { method: "PUT" }, token);
export const removeWishlistItem = (token: string, productId: string) => request<void>(`/api/v1/wishlist/items/${productId}`, { method: "DELETE" }, token);
export const fetchReviews = (productId: string) => request<ProductReviews>(`/api/v1/products/${productId}/reviews`);
export const createReview = (token: string, productId: string, input: { rating: number; title: string; comment: string }) => request<Review>(`/api/v1/products/${productId}/reviews`, { method: "POST", body: JSON.stringify(input) }, token);
export const fetchCategories = () => request<CatalogCategory[]>("/api/v1/categories");
export async function fetchSellerProducts(token: string): Promise<Product[]> {
  return (await request<ApiProduct[]>("/api/v1/seller/products", {}, token)).map(mapProduct);
}
export async function createSellerProduct(token: string, input: { name: string; categorySlug: string; description: string; price: number; stockQuantity: number }): Promise<Product> {
  return mapProduct(await request<ApiProduct>("/api/v1/seller/products", { method: "POST", body: JSON.stringify(input) }, token));
}
export async function updateSellerProduct(token: string, productId: string, input: { name: string; categorySlug: string; description: string; price: number; stockQuantity: number }): Promise<Product> {
  return mapProduct(await request<ApiProduct>(`/api/v1/seller/products/${productId}`, { method: "PUT", body: JSON.stringify(input) }, token));
}
export async function updateSellerProductStatus(token: string, productId: string, status: "DRAFT" | "ACTIVE" | "ARCHIVED"): Promise<Product> {
  return mapProduct(await request<ApiProduct>(`/api/v1/seller/products/${productId}/status`, { method: "PATCH", body: JSON.stringify({ status }) }, token));
}
export const fetchSellerOrders = (token: string) => request<SellerOrder[]>("/api/v1/seller/orders", {}, token);
export const updateSellerOrderStatus = (token: string, orderId: string, status: SellerOrder["status"]) => request<SellerOrder>(`/api/v1/seller/orders/${orderId}/status`, { method: "PATCH", body: JSON.stringify({ status }) }, token);
