import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../redux/store/hooks';
import { fetchUserOrders } from '../redux/actions';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';

const OrdersList = () => {
  const dispatch = useAppDispatch();
  const { orders, loading } = useAppSelector((state) => state.orders);

  useEffect(() => {
    // Only fetch if not already loading and orders array is empty
    if (!loading && orders.length === 0) {
      dispatch(fetchUserOrders());
    }
  }, [dispatch, loading, orders.length]);

  const getStatusText = (status: string) => {
    const statusMap: Record<string, string> = {
      PENDING: 'En attente',
      CONFIRMED: 'Confirmée',
      PROCESSING: 'En traitement',
      SHIPPED: 'Expédiée',
      DELIVERED: 'Livrée',
      CANCELLED: 'Annulée',
    };
    return statusMap[status] || status;
  };

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      CONFIRMED: 'bg-blue-100 text-blue-800',
      PROCESSING: 'bg-purple-100 text-purple-800',
      SHIPPED: 'bg-indigo-100 text-indigo-800',
      DELIVERED: 'bg-green-100 text-green-800',
      CANCELLED: 'bg-red-100 text-red-800',
    };
    return colorMap[status] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Chargement des commandes...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <h2 className="text-2xl font-bold mb-6">Mes Commandes</h2>

      {orders.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow-md">
          <p className="text-gray-600 mb-4">Vous n'avez aucune commande</p>
          <Link
            to="/products"
            className="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Voir les produits
          </Link>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold">Commande #{order.id}</h3>
                  <p className="text-sm text-gray-600">
                    {new Date(order.createdAt).toLocaleDateString('fr-FR', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </p>
                </div>
                <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(order.status)}`}>
                  {getStatusText(order.status)}
                </span>
              </div>

              <div className="border-t pt-4 mb-4">
                <div className="space-y-2 mb-4">
                  {order.items.map((item) => (
                    <div key={item.id} className="flex justify-between text-sm">
                      <span>
                        {item.product.name} x{item.quantity}
                      </span>
                      <span>{formatPrice(item.product.price * item.quantity)}€</span>
                    </div>
                  ))}
                </div>
                <div className="flex justify-between font-bold text-lg pt-2 border-t">
                  <span>Total</span>
                  <span className="text-blue-600">{formatPrice(order.totalAmount)}€</span>
                </div>
              </div>

              <Link
                to={`/order-confirmation/${order.id}`}
                className="inline-block text-blue-600 hover:text-blue-700 hover:underline text-sm font-medium"
              >
                Voir les détails →
              </Link>
            </div>
          ))}
        </div>
      )}
    </Layout>
  );
};

export default OrdersList;
