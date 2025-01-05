import { useState, useEffect } from 'react'
import { transportationService, type Transportation, type PageResponse } from '../api/transportationService'
import { TransportationModal } from './TransportationModal'
import { ConfirmDialog } from './ConfirmDialog'
import toast from 'react-hot-toast'

export function Transportations() {
  const [transportations, setTransportations] = useState<Transportation[]>([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [pageInfo, setPageInfo] = useState<Omit<PageResponse<Transportation>, 'content'> | null>(null)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false)
  const [selectedTransportation, setSelectedTransportation] = useState<Transportation | null>(null)
  const [modalMode, setModalMode] = useState<'create' | 'edit'>('create')

  const fetchTransportations = async () => {
    try {
      const response = await transportationService.getAllTransportations(page)
      setTransportations(response.content)
      setPageInfo({
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        size: response.size,
        number: response.number
      })
    } catch (error) {
      toast.error('Failed to load transportations')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchTransportations()
  }, [page])

  const handleEdit = (transportation: Transportation) => {
    setSelectedTransportation(transportation)
    setModalMode('edit')
    setIsModalOpen(true)
  }

  const handleAdd = () => {
    setSelectedTransportation(null)
    setModalMode('create')
    setIsModalOpen(true)
  }

  const handleDeleteClick = (transportation: Transportation) => {
    setSelectedTransportation(transportation)
    setIsDeleteConfirmOpen(true)
  }

  const handleDelete = async () => {
    if (!selectedTransportation) return

    try {
      await transportationService.deleteTransportation(selectedTransportation.id)
      toast.success('Transportation deleted successfully')
      fetchTransportations()
    } catch (error) {
      toast.error('Failed to delete transportation')
    }
  }

  const handleSubmit = async (data: TransportationRequest) => {
    try {
      if (modalMode === 'create') {
        await transportationService.createTransportation(data)
        toast.success('Transportation added successfully')
      } else {
        if (!selectedTransportation) return
        await transportationService.updateTransportation(selectedTransportation.id, data)
        toast.success('Transportation updated successfully')
      }
      fetchTransportations()
    } catch (error) {
      toast.error(`Failed to ${modalMode === 'create' ? 'add' : 'update'} transportation`)
      throw error
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  return (
    <div className="p-6 lg:p-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold text-slate-800">Transportations</h1>
        <button 
          onClick={handleAdd}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:ring-4 focus:ring-blue-500/50 transition-colors"
        >
          Add Transportation
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200">
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">From</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">To</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Price</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider">Duration</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-slate-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-200">
              {transportations.map((transportation) => (
                <tr key={transportation.id} className="hover:bg-slate-50">
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{transportation.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{transportation.type}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{transportation.fromLocation.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{transportation.toLocation.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">${transportation.price}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700">{transportation.durationInMinutes} min</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-700 text-right">
                    <button 
                      onClick={() => handleEdit(transportation)}
                      className="text-blue-600 hover:text-blue-800 mr-4"
                      title="Edit"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                      </svg>
                    </button>
                    <button 
                      onClick={() => handleDeleteClick(transportation)}
                      className="text-red-600 hover:text-red-800"
                      title="Delete"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
                      </svg>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {pageInfo && (
          <div className="px-6 py-4 border-t border-slate-200">
            <div className="flex items-center justify-between">
              <div className="text-sm text-slate-700">
                Showing {page * pageInfo.size + 1} to {Math.min((page + 1) * pageInfo.size, pageInfo.totalElements)} of{' '}
                {pageInfo.totalElements} results
              </div>
              <div className="flex space-x-2">
                <button
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-3 py-1 border border-slate-300 rounded-md text-sm disabled:opacity-50"
                >
                  Previous
                </button>
                <button
                  onClick={() => setPage(p => p + 1)}
                  disabled={page >= pageInfo.totalPages - 1}
                  className="px-3 py-1 border border-slate-300 rounded-md text-sm disabled:opacity-50"
                >
                  Next
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      <TransportationModal 
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleSubmit}
        initialData={selectedTransportation}
        mode={modalMode}
      />

      <ConfirmDialog
        isOpen={isDeleteConfirmOpen}
        onClose={() => setIsDeleteConfirmOpen(false)}
        onConfirm={handleDelete}
        title="Delete Transportation"
        message={`Are you sure you want to delete ${selectedTransportation?.name}? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
      />
    </div>
  )
} 