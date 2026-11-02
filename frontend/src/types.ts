export type Category = "All" | "Electronics" | "Fashion" | "Home & Living";

export type Product = {
  id: string;
  name: string;
  slug: string;
  sellerName: string;
  description: string;
  price: number;
  stockQuantity: number;
  category: Exclude<Category, "All">;
  image: string;
  status?: "DRAFT" | "ACTIVE" | "ARCHIVED";
};

export type User = { id: string; username: string; email: string; displayName: string; roles: string[] };
export type CatalogCategory = { id: string; name: string; slug: string };
export type Session = { accessToken: string; expiresAt: string; user: User };
export type CartItem = { productId: string; productName: string; productSlug: string; unitPrice: number; quantity: number; subtotal: number; availableStock: number };
export type Cart = { id: string; items: CartItem[]; totalQuantity: number; subtotal: number };
export type Order = { id: string; status: string; totalAmount: number; recipientName: string; city: string; country: string; createdAt: string; items: { productId: string; productName: string; quantity: number; unitPrice: number; subtotal: number }[] };
export type Review = { id: string; rating: number; title: string; comment: string; customerName: string; createdAt: string };
export type ProductReviews = { averageRating: number; reviewCount: number; reviews: Review[] };
export type SellerOrder = { orderId: string; status: "CONFIRMED" | "PROCESSING" | "SHIPPED" | "DELIVERED" | "CANCELLED"; sellerTotal: number; recipientName: string; city: string; country: string; createdAt: string; items: { productId: string; productName: string; quantity: number; unitPrice: number; lineTotal: number }[] };
