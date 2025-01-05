import { useState, useEffect } from 'react'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import type { AuthResponse } from '../types/auth'

export function Layout() {
  const location = useLocation()
  const navigate = useNavigate()
  const [userData, setUserData] = useState<AuthResponse | null>(null)

  useEffect(() => {
    // Get auth data from localStorage on component mount
    const authData = localStorage.getItem('authData')
    if (!authData) {
      navigate('/login')
      return
    }
    
    try {
      const parsedAuthData = JSON.parse(authData) as AuthResponse
      setUserData(parsedAuthData)
    } catch (error) {
      navigate('/login')
    }
  }, [navigate])

  const handleLogout = () => {
    localStorage.removeItem('authData')
    navigate('/login')
  }

  if (!userData) {
    return null // or a loading spinner
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* Header */}
      <header className="bg-blue-600 shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-white">Travel Planner</h1>
            
            <div className="flex items-center space-x-4">
              <span className="text-blue-100">
                Welcome, {userData.email}
              </span>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-blue-700 text-white rounded-lg hover:bg-blue-800 focus:ring-4 focus:ring-blue-500/50 transition-colors text-sm font-medium"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="flex flex-col md:flex-row min-h-[calc(100vh-64px)]">
        {/* Sidebar */}
        <div className="w-full md:w-64 bg-white border-b md:border-r border-slate-200">
          <nav className="space-y-1">
            <Link to="/locations">
              <div className={`px-6 py-4 border-b border-slate-200 ${
                location.pathname === '/locations' ? 'bg-blue-50' : ''
              }`}>
                <span className={`text-sm font-medium ${
                  location.pathname === '/locations' 
                    ? 'text-blue-600' 
                    : 'text-slate-700 hover:text-blue-600'
                } cursor-pointer transition-colors`}>
                  Locations
                </span>
              </div>
            </Link>
            <Link to="/transportations">
              <div className={`px-6 py-4 border-b border-slate-200 ${
                location.pathname === '/transportations' ? 'bg-blue-50' : ''
              }`}>
                <span className={`text-sm font-medium ${
                  location.pathname === '/transportations' 
                    ? 'text-blue-600' 
                    : 'text-slate-700 hover:text-blue-600'
                } cursor-pointer transition-colors`}>
                  Transportation
                </span>
              </div>
            </Link>
            <div className="px-6 py-4">
              <span className="text-sm font-medium text-slate-700 hover:text-blue-600 cursor-pointer transition-colors">
                Routes
              </span>
            </div>
          </nav>
        </div>

        {/* Main Area */}
        <div className="flex-1">
          <Outlet />
        </div>
      </div>
    </div>
  )
}