import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAppSelector, useAppDispatch } from '../../redux/store/hooks';
import {
  useGetCurrentUserQuery,
  useLogoutMutation,
  useGetCartQuery,
} from '../../services/apiSlice';
import { clearCredentials } from '../../redux/slices/authSlice';
import { apiSlice } from '../../services/apiSlice';

const Header = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [logoutMutation] = useLogoutMutation();
  
  const { data: currentUser } = useGetCurrentUserQuery(undefined, {
    skip: !isAuthenticated,
  });
  const { data: cart } = useGetCartQuery(undefined, {
    skip: !isAuthenticated,
  });

  const displayUser = currentUser || user;

  const cartItemsCount = cart?.items.reduce((sum, item) => sum + item.quantity, 0) || 0;

  const handleLogout = async () => {
    try {
      await logoutMutation().unwrap();
    } catch (err) {} finally {
      dispatch(clearCredentials());
      dispatch(apiSlice.util.resetApiState());
      navigate('/login');
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchKeyword.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchKeyword.trim())}`);
    } else {
      navigate('/products');
    }
  };

  return (
    <header className="bg-white shadow-md">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center">
            <span className="text-2xl font-bold text-blue-600">Carrefour</span>
          </Link>

          <div className="flex-1 max-w-xl mx-8">
            <form onSubmit={handleSearch} className="relative">
              <input
                type="text"
                placeholder="Ordinateur, jouet, cafetière..."
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                type="submit"
                className="absolute right-2 top-1/2 transform -translate-y-1/2 px-4 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Rechercher
              </button>
            </form>
          </div>

          <nav className="flex items-center gap-6">
            {isAuthenticated ? (
              <>
                <Link to="/products" className="text-gray-700 hover:text-blue-600">
                  Produits
                </Link>
                <Link to="/orders" className="text-gray-700 hover:text-blue-600 flex items-center gap-2">
                  <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
                  </svg>
                  Commandes
                </Link>
                <div className="relative">
                  <Link to="/cart" className="text-gray-700 hover:text-blue-600 flex items-center gap-2">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    Panier
                    {cartItemsCount > 0 && (
                      <span className="bg-blue-600 text-white text-xs rounded-full px-2 py-1">
                        {cartItemsCount}
                      </span>
                    )}
                  </Link>
                </div>
                <div className="flex items-center gap-2">
                  {displayUser?.lastName || displayUser?.firstName ? (
                    <span className="text-gray-700 font-medium">
                      {[displayUser?.lastName, displayUser?.firstName].filter(Boolean).join(' ')}
                    </span>
                  ) : null}
                  <button
                    onClick={handleLogout}
                    className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
                  >
                    Déconnexion
                  </button>
                </div>
              </>
            ) : (
              <>
                <Link to="/login" className="text-gray-700 hover:text-blue-600">
                  Connexion
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  Inscription
                </Link>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;

