export type Category = "All" | "Electronics" | "Fashion" | "Home & Living";

export type Product = {
  id: string;
  name: string;
  slug: string;
  sellerName: string;
  price: number;
  stockQuantity: number;
  category: Exclude<Category, "All">;
  image: string;
};

export type User = { id: string; username: string; email: string; displayName: string };
export type Session = { accessToken: string; expiresAt: string; user: User };
export type CartItem = { productId: string; productName: string; productSlug: string; unitPrice: number; quantity: number; subtotal: number; availableStock: number };
export type Cart = { id: string; items: CartItem[]; totalQuantity: number; subtotal: number };
export type Order = { id: string; status: string; totalAmount: number; recipientName: string; city: string; country: string; createdAt: string; items: { productId: string; productName: string; quantity: number; unitPrice: number; subtotal: number }[] };
