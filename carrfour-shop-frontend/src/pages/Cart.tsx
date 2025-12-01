import { Link, useNavigate } from 'react-router-dom';
import { useAppSelector } from '../redux/store/hooks';
import { useGetCartQuery, useUpdateCartItemMutation, useRemoveFromCartMutation } from '../services/apiSlice';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';

const Cart = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAppSelector((state) => state.auth);
  const { data: cart, isLoading: loading } = useGetCartQuery(undefined, {
    skip: !isAuthenticated,
  });
  const [updateCartItemMutation] = useUpdateCartItemMutation();
  const [removeFromCartMutation] = useRemoveFromCartMutation();

  const handleQuantityChange = async (itemId: number, newQuantity: number) => {
    if (newQuantity < 1) return;
    try {
      await updateCartItemMutation({ itemId, quantity: newQuantity }).unwrap();
    } catch (err) {}
  };

  const handleRemove = async (itemId: number) => {
    if (window.confirm('Voulez-vous retirer cet article du panier?')) {
      try {
        await removeFromCartMutation(itemId).unwrap();
      } catch (err) {}
    }
  };

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Chargement du panier...</p>
        </div>
      </Layout>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <Layout>
        <div className="text-center py-12">
          <h2 className="text-2xl font-bold mb-4">Votre panier est vide</h2>
          <Link to="/products" className="text-blue-600 hover:underline">
            Continuer vos achats
          </Link>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <h2 className="text-2xl font-bold mb-6">Mon Panier</h2>

      <div className="grid md:grid-cols-3 gap-8">
        <div className="md:col-span-2 space-y-4">
          {cart.items.map((item) => (
            <div key={item.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex gap-4">
                <Link to={`/products/${item.product.id}`}>
                  <div className="w-24 h-24 bg-gray-200 rounded-lg flex items-center justify-center">
                    {item.product.imageUrl ? (
                      <img
                        src={item.product.imageUrl}
                        alt={item.product.name}
                        className="w-full h-full object-cover rounded-lg"
                      />
                    ) : (
                      <span className="text-gray-400 text-xs">Pas d'image</span>
                    )}
                  </div>
                </Link>

                <div className="flex-1">
                  <Link to={`/products/${item.product.id}`}>
                    <h3 className="font-semibold text-lg hover:text-blue-600">{item.product.name}</h3>
                  </Link>
                  <p className="text-gray-600 text-sm mb-2">{item.product.description}</p>
                  <p className="text-lg font-bold text-blue-600">{formatPrice(item.product.price)}€</p>
                </div>

                <div className="flex flex-col items-end gap-2">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                      className="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100"
                    >
                      -
                    </button>
                    <span className="w-12 text-center">{item.quantity}</span>
                    <button
                      onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                      className="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-lg hover:bg-gray-100"
                    >
                      +
                    </button>
                  </div>
                  <p className="font-semibold">
                    Total: {formatPrice(item.product.price * item.quantity)}€
                  </p>
                  <button
                    onClick={() => handleRemove(item.id)}
                    className="text-red-600 hover:text-red-700 text-sm"
                  >
                    Supprimer
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="md:col-span-1">
          <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
            <h3 className="text-xl font-bold mb-4">Résumé de la commande</h3>
            <div className="space-y-2 mb-4">
              <div className="flex justify-between">
                <span>Sous-total</span>
                <span>{formatPrice(cart.totalAmount)}€</span>
              </div>
              <div className="flex justify-between font-bold text-lg pt-4 border-t">
                <span>Total</span>
                <span className="text-blue-600">{formatPrice(cart.totalAmount)}€</span>
              </div>
            </div>
            <button
              onClick={() => navigate('/order')}
              className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700"
            >
              Passer la commande
            </button>
            <Link
              to="/products"
              className="block text-center mt-4 text-blue-600 hover:underline"
            >
              Continuer vos achats
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default Cart;

