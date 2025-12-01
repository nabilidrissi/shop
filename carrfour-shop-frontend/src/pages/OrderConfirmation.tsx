import { useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useGetOrderByIdQuery, useClearCartMutation } from '../services/apiSlice';
import Layout from '../components/shared/Layout';
import { formatPrice } from '../utils/formatPrice';

const OrderConfirmation = () => {
  const { id } = useParams<{ id: string }>();
  const { data: currentOrder, isLoading: loading } = useGetOrderByIdQuery(Number(id) || 0, {
    skip: !id,
  });
  const [clearCartMutation] = useClearCartMutation();

  useEffect(() => {
    if (id) {
      clearCartMutation();
    }
  }, [id, clearCartMutation]);

  if (loading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="mt-4 text-gray-600">Chargement...</p>
        </div>
      </Layout>
    );
  }

  if (!currentOrder) {
    return (
      <Layout>
        <div className="text-center py-12">
          <p className="text-gray-600">Commande non trouvée</p>
        </div>
      </Layout>
    );
  }

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

  return (
    <Layout>
      <div className="max-w-3xl mx-auto">
        <div className="bg-white rounded-lg shadow-md p-8 text-center mb-6">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold mb-2">Commande confirmée!</h1>
          <p className="text-gray-600">Merci pour votre achat</p>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <div>
              <p className="text-sm text-gray-600">Numéro de commande</p>
              <p className="text-lg font-bold">#{currentOrder.id}</p>
            </div>
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(currentOrder.status)}`}>
              {getStatusText(currentOrder.status)}
            </span>
          </div>

          <div className="border-t pt-4 space-y-2">
            <div className="flex justify-between">
              <span className="text-gray-600">Date de commande</span>
              <span>{new Date(currentOrder.createdAt).toLocaleDateString('fr-FR')}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Email</span>
              <span>{currentOrder.email}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">Téléphone</span>
              <span>{currentOrder.phone}</span>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold mb-4">Articles commandés</h2>
          <div className="space-y-4">
            {currentOrder.items.map((item) => (
              <div key={item.id} className="flex gap-4 pb-4 border-b last:border-0">
                <div className="w-20 h-20 bg-gray-200 rounded-lg flex items-center justify-center">
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
                <div className="flex-1">
                  <h3 className="font-semibold">{item.product.name}</h3>
                  <p className="text-sm text-gray-600">Quantité: {item.quantity}</p>
                  <p className="font-semibold text-blue-600">
                    {formatPrice(item.product.price * item.quantity)}€
                  </p>
                </div>
              </div>
            ))}
          </div>
          <div className="flex justify-between font-bold text-lg pt-4 border-t mt-4">
            <span>Total</span>
            <span className="text-blue-600">{formatPrice(currentOrder.totalAmount)}€</span>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <h2 className="text-xl font-bold mb-4">Informations de livraison</h2>
          <div className="space-y-2 text-gray-600">
            <p>{currentOrder.shippingAddress}</p>
          </div>
        </div>

        <div className="text-center">
          <Link
            to="/products"
            className="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Continuer vos achats
          </Link>
        </div>
      </div>
    </Layout>
  );
};

export default OrderConfirmation;

