import type { Product } from "./types";

type ApiProduct = {
  id: string;
  name: string;
  slug: string;
  price: number;
  stockQuantity: number;
  sellerName: string;
  category: { name: Product["category"] };
};

type ProductPage = { content: ApiProduct[] };

const productImages: Record<string, string> = {
  "cafe-headphones": "/assets/headphones.png",
  "mechanical-keyboard": "/assets/keyboard.png",
  "linen-weekender": "/assets/weekender.png",
  "ceramic-table-lamp": "/assets/lamp.png"
};

export async function fetchProducts(signal: AbortSignal): Promise<Product[]> {
  const response = await fetch("/api/v1/products?size=20", { signal });
  if (!response.ok) throw new Error("Unable to load products");
  const page = (await response.json()) as ProductPage;
  return page.content.map((product) => ({
    ...product,
    category: product.category.name,
    image: productImages[product.slug] ?? "/assets/storefront-hero.png"
  }));
}
