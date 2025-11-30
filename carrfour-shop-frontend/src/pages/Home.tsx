import { Link } from 'react-router-dom';
import Layout from '../components/shared/Layout';

const Home = () => {
  return (
    <Layout>
      <div className="text-center py-12">
        <h1 className="text-4xl font-bold mb-4">Bienvenue chez Carrefour</h1>
        <p className="text-xl text-gray-600 mb-8">Découvrez notre large sélection de produits</p>
        <Link
          to="/products"
          className="inline-block px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 text-lg font-semibold"
        >
          Voir les produits
        </Link>
      </div>
    </Layout>
  );
};

export default Home;

