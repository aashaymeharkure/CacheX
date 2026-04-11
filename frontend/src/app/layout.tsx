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
