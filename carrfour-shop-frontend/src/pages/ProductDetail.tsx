import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAppSelector } from '../redux/store/hooks';
import { useGetProductByIdQuery, useAddToCartMutation } from '../services/apiSlice';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';
import { showSuccess } from '../utils/toast';

const ProductDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: currentProduct, isLoading: loading } = useGetProductByIdQuery(Number(id) || 0, {
    skip: !id,
  });
  const { isAuthenticated } = useAppSelector((state) => state.auth);
  const [addToCartMutation] = useAddToCartMutation();
  const [quantity, setQuantity] = useState(1);

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (currentProduct) {
      try {
        await addToCartMutation({ productId: currentProduct.id, quantity }).unwrap();
        showSuccess('Produit ajouté au panier avec succès');
      } catch (err) {}
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Chargement du produit...</p>
        </div>
      </Layout>
    );
  }

  if (!currentProduct) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-gray-600">Produit non trouvé</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <button
        onClick={() => navigate(-1)}
        className="mb-4 text-blue-600 hover:underline"
      >
        ← Retour
      </button>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="grid md:grid-cols-2 gap-8 p-8">
          <div className="h-96 bg-gray-200 flex items-center justify-center rounded-lg">
            {currentProduct.imageUrl ? (
              <img
                src={currentProduct.imageUrl}
                alt={currentProduct.name}
                className="h-full w-full object-cover rounded-lg"
              />
            ) : (
              <span className="text-gray-400">Pas d'image</span>
            )}
          </div>

          <div>
            <h1 className="text-3xl font-bold mb-4">{currentProduct.name}</h1>
            <p className="text-gray-600 mb-4">{currentProduct.description}</p>
            <div className="mb-4">
              <span className="text-3xl font-bold text-blue-600">{formatPrice(currentProduct.price)}€</span>
            </div>

            <div className="mb-4">
              <span className="text-sm text-gray-600">Catégorie: </span>
              <span className="text-sm font-medium">{currentProduct.category}</span>
            </div>

            <div className="mb-4">
              <span className="text-sm text-gray-600">Stock: </span>
              <span className={`text-sm font-medium ${currentProduct.stock > 0 ? 'text-green-600' : 'text-red-600'}`}>
                {currentProduct.stock > 0 ? `${currentProduct.stock} disponible(s)` : 'Rupture de stock'}
              </span>
            </div>

            {currentProduct.stock > 0 && (
              <div className="mb-6">
                <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-2">
                  Quantité
                </label>
                <input
                  type="number"
                  id="quantity"
                  min="1"
                  max={currentProduct.stock}
                  value={quantity}
                  onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
                  className="w-20 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            )}

            <button
              onClick={handleAddToCart}
              disabled={currentProduct.stock === 0}
              className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {currentProduct.stock === 0 ? 'Rupture de stock' : 'Ajouter au panier'}
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default ProductDetail;

