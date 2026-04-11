/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#0a0a0a",
        foreground: "#ffffff",
        aics_gray: "#888888",
        aics_green: "#00cc66",
        aics_red: "#ff3333",
        aics_yellow: "#ffcc00"
      },
    },
  },
  plugins: [],
};
