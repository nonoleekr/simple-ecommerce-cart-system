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
