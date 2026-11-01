import { useEffect, useMemo, useState } from "react";
import { fetchProducts } from "./api";
import { featuredProducts } from "./data";
import type { Category, Product } from "./types";

const categories: Category[] = ["All", "Electronics", "Fashion", "Home & Living"];

function SearchIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m16 16 4 4"/></svg>;
}

function CartIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 4h2l2 11h10l2-7H6"/><circle cx="9" cy="19" r="1.5"/><circle cx="17" cy="19" r="1.5"/></svg>;
}

function ArrowIcon() {
  return <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14M14 7l5 5-5 5"/></svg>;
}

type HeaderProps = {
  cartCount: number;
  query: string;
  onQueryChange: (value: string) => void;
};

function Header({ cartCount, query, onQueryChange }: HeaderProps) {
  return (
    <header className="site-header">
      <a className="brand" href="#top" aria-label="Cartiva home">
        <span className="brand-mark" aria-hidden="true" />
        Cartiva
      </a>
      <nav aria-label="Primary navigation">
        <a className="active" href="#products">Discover</a>
        <a href="#products">Categories</a>
        <a href="#orders">Orders</a>
      </nav>
      <label className="search-field">
        <span className="sr-only">Search products</span>
        <SearchIcon />
        <input
          value={query}
          onChange={(event) => onQueryChange(event.target.value)}
          placeholder="Search products"
        />
      </label>
      <button className="header-cart" type="button" aria-label={`Cart with ${cartCount} items`}>
        <CartIcon />
        {cartCount > 0 ? <span>{cartCount}</span> : null}
      </button>
      <button className="account-button" type="button" aria-label="Account menu">
        <span aria-hidden="true">A</span>
        <b>Alex</b>
      </button>
    </header>
  );
}

function Hero() {
  return (
    <section className="hero" id="top">
      <div className="hero-copy">
        <h1>Find something<br />worth keeping</h1>
        <p>Independent sellers, useful finds, and a checkout that stays simple.</p>
        <a className="primary-action" href="#products">Explore products <ArrowIcon /></a>
      </div>
      <img src="/assets/storefront-hero.png" alt="Headphones, keyboard, weekender bag, and table lamp" />
    </section>
  );
}

type ProductCardProps = { product: Product; onAdd: () => void };

function ProductCard({ product, onAdd }: ProductCardProps) {
  const lowStock = product.stockQuantity <= 4;
  return (
    <article className="product-card">
      <div className="product-image-wrap">
        <img src={product.image} alt={product.name} />
        <span className="corner-detail" aria-hidden="true" />
      </div>
      <div className="product-content">
        <h3>{product.name}</h3>
        <p className="seller">By {product.sellerName}</p>
        <div className="product-footer">
          <div>
            <strong>{new Intl.NumberFormat("en-MY", { style: "currency", currency: "MYR" }).format(product.price)}</strong>
            <p className={lowStock ? "stock low" : "stock"}>
              <span aria-hidden="true" /> {lowStock ? `Only ${product.stockQuantity} left` : "In stock"}
            </p>
          </div>
          <button type="button" onClick={onAdd} aria-label={`Add ${product.name} to cart`}>
            <CartIcon />
          </button>
        </div>
      </div>
    </article>
  );
}

type ProductSectionProps = {
  products: Product[];
  selectedCategory: Category;
  onCategoryChange: (category: Category) => void;
  sort: string;
  onSortChange: (sort: string) => void;
  onAdd: (product: Product) => void;
};

function ProductSection(props: ProductSectionProps) {
  return (
    <section className="products-section" id="products">
      <div className="product-toolbar">
        <h2>Fresh finds</h2>
        <div className="category-list" aria-label="Product categories">
          {categories.map((category) => (
            <button
              className={props.selectedCategory === category ? "selected" : ""}
              type="button"
              key={category}
              onClick={() => props.onCategoryChange(category)}
            >
              {category}
            </button>
          ))}
        </div>
        <select value={props.sort} onChange={(event) => props.onSortChange(event.target.value)} aria-label="Sort products">
          <option value="newest">Newest</option>
          <option value="price-asc">Price: low to high</option>
          <option value="price-desc">Price: high to low</option>
          <option value="name">Name</option>
        </select>
      </div>
      {props.products.length > 0 ? (
        <div className="product-grid">
          {props.products.map((product) => (
            <ProductCard key={product.id} product={product} onAdd={() => props.onAdd(product)} />
          ))}
        </div>
      ) : <p className="empty-state">No products match your search.</p>}
    </section>
  );
}

export function App() {
  const [products, setProducts] = useState(featuredProducts);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<Category>("All");
  const [sort, setSort] = useState("newest");
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    const controller = new AbortController();
    fetchProducts(controller.signal)
      .then((loaded) => { if (loaded.length > 0) setProducts(loaded); })
      .catch(() => undefined);
    return () => controller.abort();
  }, []);

  const visibleProducts = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    const filtered = products.filter((product) =>
      (category === "All" || product.category === category) &&
      (normalized === "" || product.name.toLowerCase().includes(normalized))
    );
    return [...filtered].sort((left, right) => {
      if (sort === "price-asc") return left.price - right.price;
      if (sort === "price-desc") return right.price - left.price;
      if (sort === "name") return left.name.localeCompare(right.name);
      return 0;
    });
  }, [products, category, query, sort]);

  return (
    <div className="app-shell">
      <Header cartCount={cartCount} query={query} onQueryChange={setQuery} />
      <main>
        <Hero />
        <ProductSection
          products={visibleProducts}
          selectedCategory={category}
          onCategoryChange={setCategory}
          sort={sort}
          onSortChange={setSort}
          onAdd={() => setCartCount((count) => count + 1)}
        />
      </main>
    </div>
  );
}
