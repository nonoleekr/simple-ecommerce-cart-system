import type { Product } from "./types";

export const featuredProducts: Product[] = [
  {
    id: "demo-headphones",
    name: "Café Headphones",
    slug: "cafe-headphones",
    sellerName: "Soundfold",
    description: "Comfortable wireless headphones tuned for clear, balanced everyday listening.",
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
    description: "A compact mechanical keyboard with tactile switches and a sturdy aluminum frame.",
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
    description: "A durable canvas weekender with organized storage for short trips and daily carry.",
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
    description: "A softly lit ceramic table lamp designed to bring warm light to compact spaces.",
    price: 189,
    stockQuantity: 9,
    category: "Home & Living",
    image: "/assets/lamp.png"
  }
];
