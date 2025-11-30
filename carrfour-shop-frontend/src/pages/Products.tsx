import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../redux/store/hooks';
import { fetchProducts, addToCart } from '../redux/actions';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';

const Products = () => {
  const dispatch = useAppDispatch();
  const { products, loading } = useAppSelector((state) => state.products);
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  useEffect(() => {
    // Only fetch if not already loading and products array is empty
    if (!loading && products.length === 0) {
      dispatch(fetchProducts());
    }
  }, [dispatch, loading, products.length]);

  const handleAddToCart = (productId: number) => {
    if (!isAuthenticated) {
      alert('Veuillez vous connecter pour ajouter des produits au panier');
      return;
    }
    dispatch(addToCart({ productId, quantity: 1 }));
  };

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Chargement des produits...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map((product) => (
          <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
            <Link to={`/products/${product.id}`}>
              <div className="h-48 bg-gray-200 flex items-center justify-center">
                {product.imageUrl ? (
                  <img src={product.imageUrl} alt={product.name} className="h-full w-full object-cover" />
                ) : (
                  <span className="text-gray-400">Pas d'image</span>
                )}
              </div>
            </Link>
            <div className="p-4">
              <Link to={`/products/${product.id}`}>
                <h3 className="font-semibold text-lg mb-2 hover:text-blue-600">{product.name}</h3>
              </Link>
              <p className="text-gray-600 text-sm mb-2 line-clamp-2">{product.description}</p>
              <div className="flex items-center justify-between">
                <span className="text-xl font-bold text-blue-600">{formatPrice(product.price)}€</span>
                <button
                  onClick={() => handleAddToCart(product.id)}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-sm"
                >
                  + Ajouter
                </button>
              </div>
              {product.stock < 10 && (
                <p className="text-sm text-red-600 mt-2">Stock limité</p>
              )}
            </div>
          </div>
        ))}
      </div>

      {products.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-600">Aucun produit trouvé</p>
        </div>
      )}
    </Layout>
  );
};

export default Products;

