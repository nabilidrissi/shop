import currency from 'currency.js';

export const formatPrice = (price: number | undefined | null): string => {
  if (price === undefined || price === null || isNaN(price)) {
    return currency(0, { symbol: '', decimal: '.', separator: ' ' }).format();
  }
  return currency(price, { symbol: '', decimal: '.', separator: ' ' }).format();
};
