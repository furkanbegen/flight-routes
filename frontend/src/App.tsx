import { BrowserRouter as Router, Routes as RouterRoutes, Route, Navigate } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import { Layout } from './components/Layout'
import { Login } from './components/Login'
import { Locations } from './components/Locations'
import { Transportations } from './components/Transportations'
import { Routes } from './components/Routes'
import { ProtectedRoute } from './components/ProtectedRoute'

function App() {
  return (
    <Router>
      <Toaster position="top-right" />
      <RouterRoutes>
        <Route path="/login" element={<Login />} />
        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route index element={<Navigate to="/locations" replace />} />
          <Route path="locations" element={<Locations />} />
          <Route path="transportations" element={<Transportations />} />
          <Route path="routes" element={<Routes />} />
        </Route>
        <Route path="*" element={<Navigate to="/login" replace />} />
      </RouterRoutes>
    </Router>
  )
}

export default App
