import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../redux/store/hooks';
import { fetchCart, createOrder } from '../redux/actions';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';

const Order = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { cart, loading: cartLoading } = useAppSelector((state) => state.cart);
  const { user } = useAppSelector((state) => state.auth);
  const { loading, error } = useAppSelector((state) => state.orders);

  const [formData, setFormData] = useState({
    shippingAddress: '',
    billingAddress: '',
    phone: user?.phone || '',
    email: user?.email || '',
  });

  useEffect(() => {
    // Only fetch if cart is not loaded yet and not currently loading
    if (!cart && !cartLoading) {
      dispatch(fetchCart()).then((result) => {
        if (fetchCart.fulfilled.match(result)) {
          if (!result.payload || result.payload.items.length === 0) {
            navigate('/cart');
          }
        }
      });
    } else if (cart && cart.items.length === 0) {
      // If cart is loaded but empty, redirect immediately
      navigate('/cart');
    }
  }, [dispatch, navigate, cart, cartLoading]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    const result = await dispatch(createOrder(formData));
    if (createOrder.fulfilled.match(result)) {
      navigate(`/order-confirmation/${result.payload.id}`);
    }
  };

  if (!cart || cart.items.length === 0) {
    return null;
  }

  return (
    <Layout>
      <h2 className="text-2xl font-bold mb-6">Finaliser la commande</h2>

      {error && (
        <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      <div className="grid md:grid-cols-3 gap-8">
        <div className="md:col-span-2">
          <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-4">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                id="email"
                name="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label htmlFor="phone" className="block text-sm font-medium text-gray-700 mb-1">
                Téléphone
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                required
                value={formData.phone}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label htmlFor="shippingAddress" className="block text-sm font-medium text-gray-700 mb-1">
                Adresse de livraison
              </label>
              <textarea
                id="shippingAddress"
                name="shippingAddress"
                required
                rows={3}
                value={formData.shippingAddress}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label htmlFor="billingAddress" className="block text-sm font-medium text-gray-700 mb-1">
                Adresse de facturation
              </label>
              <textarea
                id="billingAddress"
                name="billingAddress"
                required
                rows={3}
                value={formData.billingAddress}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {loading ? 'Traitement...' : 'Confirmer la commande'}
            </button>
          </form>
        </div>

        <div className="md:col-span-1">
          <div className="bg-white rounded-lg shadow-md p-6 sticky top-4">
            <h3 className="text-xl font-bold mb-4">Résumé</h3>
            <div className="space-y-2 mb-4">
              {cart.items.map((item) => (
                <div key={item.id} className="flex justify-between text-sm">
                  <span>
                    {item.product.name} x{item.quantity}
                  </span>
                  <span>{formatPrice(item.product.price * item.quantity)}€</span>
                </div>
              ))}
            </div>
            <div className="flex justify-between font-bold text-lg pt-4 border-t">
              <span>Total</span>
              <span className="text-blue-600">{formatPrice(cart.totalAmount)}€</span>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default Order;

