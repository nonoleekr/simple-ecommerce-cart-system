import type { Product } from "./types";

export const featuredProducts: Product[] = [
  {
    id: "demo-headphones",
    name: "Café Headphones",
    slug: "cafe-headphones",
    sellerName: "Soundfold",
    price: 429,
    stockQuantity: 12,
    category: "Electronics",
    image: "/assets/headphones.png"
  },
  {
    id: "demo-keyboard",
    name: "Mechanical Keyboard",
    slug: "mechanical-keyboard",
    sellerName: "KeyStudio",
    price: 299,
    stockQuantity: 18,
    category: "Electronics",
    image: "/assets/keyboard.png"
  },
  {
    id: "demo-weekender",
    name: "Linen Weekender",
    slug: "linen-weekender",
    sellerName: "Thread & Co.",
    price: 359,
    stockQuantity: 4,
    category: "Fashion",
    image: "/assets/weekender.png"
  },
  {
    id: "demo-lamp",
    name: "Ceramic Table Lamp",
    slug: "ceramic-table-lamp",
    sellerName: "Homeform",
    price: 189,
    stockQuantity: 9,
    category: "Home & Living",
    image: "/assets/lamp.png"
  }
];
