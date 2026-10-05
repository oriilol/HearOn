import CoreImage
import SwiftUI
import UIKit

extension View {
    /// System font (San Francisco), scaled with Dynamic Type.
    func appFont(_ style: Font.TextStyle, _ weight: Font.Weight = .regular) -> some View {
        font(.system(style, weight: weight))
    }
}

/// Port of `getDominantColor`: average color of the cover, lightness clamped
/// to 25–75 % so it works as an accent in both light and dark mode.
enum DominantColor {
    private static let context = CIContext(options: [.workingColorSpace: NSNull()])

    static func from(_ url: URL) async -> Color? {
        guard let (data, _) = try? await URLSession.shared.data(from: url),
              let image = CIImage(data: data) else { return nil }
        let filter = CIFilter(name: "CIAreaAverage", parameters: [kCIInputImageKey: image, kCIInputExtentKey: CIVector(cgRect: image.extent)])
        guard let output = filter?.outputImage else { return nil }
        var px = [UInt8](repeating: 0, count: 4)
        context.render(output, toBitmap: &px, rowBytes: 4, bounds: CGRect(x: 0, y: 0, width: 1, height: 1), format: .RGBA8, colorSpace: nil)
        var h: CGFloat = 0, s: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        UIColor(red: CGFloat(px[0]) / 255, green: CGFloat(px[1]) / 255, blue: CGFloat(px[2]) / 255, alpha: 1)
            .getHue(&h, saturation: &s, brightness: &b, alpha: &a)
        return Color(hue: h, saturation: min(1, s * 1.2), brightness: min(0.85, max(0.35, b)))
    }
}

extension ThemeMode {
    var colorScheme: ColorScheme? {
        switch self {
        case .system: nil
        case .light: .light
        case .dark: .dark
        }
    }
}
