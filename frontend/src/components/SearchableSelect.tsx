import { Fragment, useState, useEffect } from 'react'
import { Combobox, Transition } from '@headlessui/react'

interface Option {
  id: number
  name: string
}

interface SearchableSelectProps {
  value: Option | null
  onChange: (value: Option | null) => void
  onSearch: (query: string) => Promise<Option[]>
  placeholder?: string
  label: string
  error?: string
}

export function SearchableSelect({
  value,
  onChange,
  onSearch,
  placeholder = 'Search...',
  label,
  error
}: SearchableSelectProps) {
  const [query, setQuery] = useState('')
  const [options, setOptions] = useState<Option[]>([])
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    const timeoutId = setTimeout(async () => {
      if (query.length >= 2) {
        setIsLoading(true)
        try {
          const results = await onSearch(query)
          setOptions(results)
        } catch (error) {
          console.error('Failed to search:', error)
        } finally {
          setIsLoading(false)
        }
      }
    }, 300)

    return () => clearTimeout(timeoutId)
  }, [query, onSearch])

  const handleClear = () => {
    setQuery('')
    onChange(null)
    setOptions([])
  }

  return (
    <div className="relative">
      <Combobox value={value} onChange={onChange}>
        <Combobox.Label className="block text-sm font-medium text-gray-700">
          {label}
        </Combobox.Label>
        
        <div className="relative mt-1">
          <Combobox.Input
            className={`w-full rounded-md border ${
              error ? 'border-red-300' : 'border-gray-300'
            } px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 pr-20`}
            displayValue={(option: Option | null) => option?.name ?? ''}
            onChange={(event) => setQuery(event.target.value)}
            placeholder={placeholder}
          />
          
          <div className="absolute inset-y-0 right-0 flex items-center pr-2 space-x-1">
            {(value || query) && (
              <button
                type="button"
                onClick={handleClear}
                className="p-1 text-gray-400 hover:text-gray-500"
              >
                <svg 
                  className="h-5 w-5" 
                  viewBox="0 0 20 20" 
                  fill="currentColor"
                  aria-hidden="true"
                >
                  <path
                    d="M6.28 5.22a.75.75 0 00-1.06 1.06L8.94 10l-3.72 3.72a.75.75 0 101.06 1.06L10 11.06l3.72 3.72a.75.75 0 101.06-1.06L11.06 10l3.72-3.72a.75.75 0 00-1.06-1.06L10 8.94 6.28 5.22z"
                  />
                </svg>
              </button>
            )}

            <Combobox.Button className="flex items-center">
              {isLoading ? (
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-blue-600" />
              ) : (
                <svg
                  className="h-5 w-5 text-gray-400"
                  viewBox="0 0 20 20"
                  fill="none"
                  stroke="currentColor"
                >
                  <path
                    d="M7 7l3-3 3 3m0 6l-3 3-3-3"
                    strokeWidth="1.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              )}
            </Combobox.Button>
          </div>

          <Transition
            as={Fragment}
            leave="transition ease-in duration-100"
            leaveFrom="opacity-100"
            leaveTo="opacity-0"
          >
            <Combobox.Options className="absolute z-10 mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none sm:text-sm">
              {options.length === 0 && query.length >= 2 ? (
                <div className="relative cursor-default select-none px-4 py-2 text-gray-700">
                  {isLoading ? 'Loading...' : 'No locations found.'}
                </div>
              ) : (
                options.map((option) => (
                  <Combobox.Option
                    key={option.id}
                    className={({ active }) =>
                      `relative cursor-default select-none py-2 pl-10 pr-4 ${
                        active ? 'bg-blue-600 text-white' : 'text-gray-900'
                      }`
                    }
                    value={option}
                  >
                    {({ selected, active }) => (
                      <>
                        <span className={`block truncate ${selected ? 'font-medium' : 'font-normal'}`}>
                          {option.name}
                        </span>
                        {selected ? (
                          <span
                            className={`absolute inset-y-0 left-0 flex items-center pl-3 ${
                              active ? 'text-white' : 'text-blue-600'
                            }`}
                          >
                            <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                              <path
                                fillRule="evenodd"
                                d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                                clipRule="evenodd"
                              />
                            </svg>
                          </span>
                        ) : null}
                      </>
                    )}
                  </Combobox.Option>
                ))
              )}
            </Combobox.Options>
          </Transition>
        </div>
      </Combobox>
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  )
} 