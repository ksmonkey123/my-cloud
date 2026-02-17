export class ErrorUtils {

  public static readHttpError(error: any): string[] {
    if (error) {
      if (error.error && error.error.message) {
        return [`Status: ${error.status}`, `Message: ${error.error.message}`]
      }
      return [`Status: ${error.status}`]
    }
    return [error];
  }
}
