
  Pod::Spec.new do |s|
    s.name = 'CapacitorBackgroundService'
    s.version = '0.0.1'
    s.summary = 'Servicios en segundo plano'
    s.license = 'MIT'
    s.homepage = 'https://github.com/Edersj02/capacitor-bakground-service.git'
    s.author = 'Eder Santa Cruz'
    s.source = { :git => 'https://github.com/Edersj02/capacitor-bakground-service.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end