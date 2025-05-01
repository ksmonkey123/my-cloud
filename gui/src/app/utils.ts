/**
 * Convert a JS Date Instance to a date string of the format yyyy-MM-dd
 *
 * This strips away all timezone information, which is needed for REST services
 */
export function toDateString(date: Date): string {
  const d = new Date(date);

  let year = d.getFullYear();
  let month = '' + (d.getMonth() + 1);
  let day = '' + d.getDate();

  if (month.length < 2)
    month = '0' + month;
  if (day.length < 2)
    day = '0' + day;

  return [year, month, day].join('-');
}
