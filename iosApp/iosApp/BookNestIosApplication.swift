import SwiftUI
import UIKit
import Shared

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

private struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

@main
struct BookNestIosApplication: App {
    init() {
        KoinInitializerKt.initializeKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
