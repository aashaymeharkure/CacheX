import type { Metadata } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'CacheX',
  description: 'Adaptive Intelligent Cache Management System',
  icons: {
    icon: '/favicon.png',
  },
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className="bg-background text-foreground h-screen w-screen overflow-hidden">
        {children}
      </body>
    </html>
  )
}
